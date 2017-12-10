# Copyright 2017 NYU Big Data Application Development. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
Your Personal Neighborhood Service

Paths:
------
GET / - Displays the index page for the neighborhood service
POST /questionnaire - User fill in the questionnaire form and send the input data to the backend
GET /recommendations/{cluster_id} - returns recommendations of locations in the specified cluster
"""

import os
import sys
import logging
from flask import Flask, jsonify, request, json, url_for, make_response, abort
from flask_api import status  # HTTP Status Codes
from werkzeug.exceptions import NotFound
from . import app

import pyspark
from numpy import array
from math import sqrt
from pyspark.mllib.clustering import KMeans, KMeansModel
from pyspark.sql import SQLContext
import pickle

# Error handlers require app to be initialized so we must import
# then only after we have initialized the Flask app instance

######################################################################
# Error Handlers
######################################################################
@app.errorhandler(400)
def bad_request(error):
    """ Handles bad requests with 400_BAD_REQUEST """
    message = error.message or str(error)
    app.logger.info(message)
    return jsonify(status=400, error='Bad Request', message=message), 400


@app.errorhandler(404)
def not_found(error):
    """ Handles resources not found with 404_NOT_FOUND """
    message = error.message or str(error)
    app.logger.info(message)
    return jsonify(status=404, error='Not Found', message=message), 404


@app.errorhandler(405)
def method_not_supported(error):
    """ Handles unsupported HTTP methods with 405_METHOD_NOT_SUPPORTED """
    message = error.message or str(error)
    app.logger.info(message)
    return jsonify(status=405, error='Method not Allowed', message=message), 405


@app.errorhandler(415)
def mediatype_not_supported(error):
    """ Handles unsupported media requests with 415_UNSUPPORTED_MEDIA_TYPE """
    message = error.message or str(error)
    app.logger.info(message)
    return jsonify(status=415, error='Unsupported media type', message=message), 415


@app.errorhandler(500)
def internal_server_error(error):
    """ Handles unexpected server error with 500_SERVER_ERROR """
    message = error.message or str(error)
    app.logger.info(message)
    return jsonify(status=500, error='Internal Server Error', message=message), 500


######################################################################
# GET INDEX
######################################################################
@app.route('/', methods=['GET'])
def index():
    """ Root URL response """
    return app.send_static_file('index.html')

######################################################################
# Create a feed through user completing questionnaire
######################################################################
@app.route('/questionnaire', methods=['POST'])
def complete_questionnaire():
    generder = str(request.args.get('generder'))
    sexOri = str(request.args.get['sexualOrientation'])
    race = str(request.args.get['race'])
    age = str(request.args.get['age'])
    crime = str(request.args.get['crime'])
    price = int(request.args.get['eco'])

    # store input feed to the model which is converted from userInput
    feed = {}

    # demographic:
    female_min = 0.28
    female_max = 0.57
    male_min = 0.43
    male_max = 0.72
    if generder == 'male':
        if sexOri == 'straight' or 'lesbian':
            feed['female'] = female_max
            feed['male'] = male_min
        elif sexOri == 'gay':
            feed['female'] = female_min
            feed['male'] = male_max
        else:
            feed['female'] = 0.5
            feed['male'] = 0.5
    elif generder == 'female':
        if sexOri == 'straight' or 'gay':
            feed['female'] = female_min
            feed['male'] = male_max
        elif sexOri == 'lesbian':
            feed['female'] = female_max
            feed['male'] = female_min
        else:
            feed['female'] = 0.5
            feed['male'] = 0.5
    else:
        feed['female'] = 0.5
        feed['male'] = 0.5

    # race features:
    if race == 'white':
        feed['white'] = 0.85
        feed['black'] = 0.05
        feed['asian'] = 0.05
        feed['hispanic'] = 0.05
    elif race == 'asian':
        feed['asian'] = 0.15
        feed['white'] = 0.5
        feed['black'] = 0.05
        feed['hispanic'] = 0.05
    elif race == 'black':
        feed['black'] = 0.4
        feed['white'] = 0.5
        feed['asian'] = 0.05
        feed['hispanic'] = 0.05
    elif race == 'hispanic':
        feed['hispanic'] = 0.4
        feed['white'] = 0.5
        feed['asian'] = 0.05
        feed['black'] = 0.05
    else:
        feed['white'] = 0.2
        feed['asian'] = 0.2
        feed['black'] = 0.2
        feed['hispanic'] = 0.2

    # age features
    if age == 'young':
        feed['young'] = 0.65
        feed['mid'] = 0.15
        feed['senior'] = 0.15
    elif age == 'mid':
        feed['young'] = 0.5
        feed['mid'] = 0.25
        feed['senior'] = 0.15
    elif age == 'senior':
        feed['young'] = 0.5
        feed['mid'] = 0.15
        feed['senior'] = 0.28
    else:
        feed['young'] = 0.5
        feed['mid'] = 0.2
        feed['senior'] = 0.2

    # crime features
    if crime == 'low':
        feed['crime'] = 4
    elif crime == 'midiumlow':
        feed['crime'] = 450
    elif crime == 'midium':
        feed['crime'] = 895
    elif crime == 'midiumhigh':
        feed['crime'] = 1340
    elif crime == 'high':
        feed['crime'] = 1791

    # price features
    feed['price'] = price

    cluster_id = getCluster(feed)
    app.redirect('/recommendations/' + cluster_id, code=302)


######################################################################
# Get All Recommendations for the User
######################################################################
@app.route('/recommendations/<int:cluster_id>', methods=['GET'])
def get_recommendations(cluster_id):



######################################################################
#  U T I L I T Y   F U N C T I O N S
######################################################################

def getCluster(feed):
    sc = SparkContext()
    sqlContext = SQLContext(sc)

    price = feed['price']
    crime = feed['crime']
    male = feed['male']
    female = feed['female']
    white = feed['white']
    black = feed['black']
    asian = feed['asian']
    hispanic = feed['hispanic']
    young = feed['young']
    mid = feed['mid']
    senior = feed['senior']

    KModel = KMeansModel.load(sc, "project/data/output/KMeansModel");
    cluster_id = KModel.predict(price, crime, male, female, white, black, asian, hispanic, young, mid, senior)
    return cluster_id


def check_content_type(content_type):
    """ Checks that the media type is correct """
    if request.headers['Content-Type'] == content_type:
        return
    app.logger.error('Invalid Content-Type: %s', request.headers['Content-Type'])
    abort(status.HTTP_415_UNSUPPORTED_MEDIA_TYPE, 'Content-Type must be {}'.format(content_type))


def initialize_logging(log_level=logging.INFO):
    """ Initialized the default logging to STDOUT """
    if not app.debug:
        print 'Setting up logging...'
        # Set up default logging for submodules to use STDOUT
        # datefmt='%m/%d/%Y %I:%M:%S %p'
        fmt = '[%(asctime)s] %(levelname)s in %(module)s: %(message)s'
        logging.basicConfig(stream=sys.stdout, level=log_level, format=fmt)
        # Make a new log handler that uses STDOUT
        handler = logging.StreamHandler(sys.stdout)
        handler.setFormatter(logging.Formatter(fmt))
        handler.setLevel(log_level)
        # Remove the Flask default handlers and use our own
        handler_list = list(app.logger.handlers)
        for log_handler in handler_list:
            app.logger.removeHandler(log_handler)
        app.logger.addHandler(handler)
        app.logger.setLevel(log_level)
        app.logger.info('Logging handler established')
