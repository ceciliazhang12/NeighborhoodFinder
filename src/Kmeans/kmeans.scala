
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext

val sqlContext = new SQLContext(sc)

// Load and parse the data
val datas = sqlContext.read
  .load("project/data/JoinedData2/*")


val selectedData = datas.select("2017-09", "Crime_Rate_Per_100000", "male", "female", "white", "black", "asian", "hispanic")
val rdd = selectedData.map(x=> Vectors.dense(x.getDouble(0),x.getDouble(1), x.getDouble(2), x.getDouble(3),x.getDouble(4), x.getDouble(5), x.getDouble(6), x.getDouble(7) ))

//val data = sc.textFile("data/mllib/kmeans_data.txt")
//val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

// Cluster the data into two classes using KMeans
val numClusters = 100
val numIterations = 20
val clusters = KMeans.train(rdd, numClusters, numIterations)

// Evaluate clustering by computing Within Set Sum of Squared Errors
val WSSSE = clusters.computeCost(rdd)
println("Within Set Sum of Squared Errors = " + WSSSE)

// Save and load model
clusters.save(sc, "target/org/apache/spark/KMeansExample/KMeansModel")
val sameModel = KMeansModel.load(sc, "target/org/apache/spark/KMeansExample/KMeansModel")