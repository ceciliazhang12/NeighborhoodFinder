import pyspark

from numpy import array
from math import sqrt
from pyspark.mllib.clustering import KMeans, KMeansModel
from pyspark.sql import SQLContext

sc = SparkContext()
sqlContext = SQLContext(sc)

def getCluster(price, crime, male, female, white, black, asian, hispanic):
    KModel = KMeansModel.load(sc, "project/data/output/KMeansModel");
    cluster = KModel.predict([price, crime, male, female, white, black, asian, hispanic])
    return cluster

if __name__ == '__main__':
    print(getCluster(2000,22,1,0,0,0,1,0))
