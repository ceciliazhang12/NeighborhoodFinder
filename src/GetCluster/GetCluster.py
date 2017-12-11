import pyspark

from numpy import array
from math import sqrt
from pyspark.mllib.clustering import KMeans, KMeansModel
from pyspark.sql import SQLContext
import pandas
import cPickle as pickle

sc = SparkContext()
sqlContext = SQLContext(sc)

def getCluster(price, crime, male, female, white, black, asian, hispanic, young, mid_age, senior):
    KModel = KMeansModel.load(sc, "project/data/output/KMeansModel");
    cluster = KModel.predict([price, crime, male, female, white, black, asian, hispanic, young, mid_age, senior])
    return cluster

def getDicFromPreDF() :
    preDF = sqlContext.read.parquet("project/data/output/Cluster")
    dict = preDF.set_index('Cluster').to_dict('index')
    pickle.dump(dict, open("dict.p", "wb"))



if __name__ == '__main__':
    print(getCluster(2000,22,1,0,0,0,1,0,1,0,0))
