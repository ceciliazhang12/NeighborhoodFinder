
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext


object Kmeans {
  def main(args: Array[String]): Unit = {
    val sqlContext = new SQLContext(sc)

    // Load and parse the data
    val datas = sqlContext.read
      .load("project/data/JoinedData2/*")

    val selectedData = datas.select("2017-09", "Crime_Rate_Per_100000", "male", "female", "white", "black", "asian", "hispanic", "young", "mid_age", "senior")
    val rdd = selectedData.map(x => Vectors.dense(
      x.getDouble(0),
      x.getDouble(1),
      x.getDouble(2),
      x.getDouble(3),
      x.getDouble(4),
      x.getDouble(5),
      x.getDouble(6),
      x.getDouble(7),
      x.getDouble(8),
      x.getDouble(9),
      x.getDouble(10))).cache()
    

    // Cluster the data into two classes using KMeans
    val numClusters = 1000
    val numIterations = 20
    val clusters = KMeans.train(rdd, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(rdd)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    // get prediction
    val predictions = datas.rdd.map{r =>(r.getInt(0), r.getString(1), r.getString(2), r.getString(3),
      clusters.predict(Vectors.dense(r.getDouble(4), r.getDouble(5), r.getDouble(6),r.getDouble(7),
        r.getDouble(8),r.getDouble(9), r.getDouble(10),r.getDouble(11), r.getDouble(12), r.getDouble(13), r.getDouble(14))))}

    val predDF = predictions.toDF("RegionName","City", "State", "CountyName", "Cluster");

    predDF.write.save("project/data/output/Cluster")
    // Save and load model
    clusters.save(sc, "project/data/output/KMeansModel")
    val sameModel = KMeansModel.load(sc, "project/data/output/KMeansModel")
  }
}