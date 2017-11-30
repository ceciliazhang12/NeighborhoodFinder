// start spark shell by using following command: spark-shell --master local --packages com.databricks:spark-csv_2.10:1.5.0

import org.apache.spark.sql.SQLContext

import scala.collection.mutable.ArrayBuffer

object Join {
  def main(args: Array[String]) {

    case class X(County: String, State: String, Crime_Rate_Per_100000: Double)

    val sqlContext = new SQLContext(sc)

    val crime_origin_dataset = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true").load("project/data/crime_data_w_population_and_crime_rate.csv")

    val county = crime_origin_dataset.select($"county_name").map(x => x.getString(0).split(",")(0)).map(x => if (x.indexOf("County") != -1) x.substring(0, x.indexOf("County") - 1) else x).map(x => if (x.indexOf("city") != -1) x.substring(0, x.indexOf("city") - 1) else x).map(x => if (x.indexOf("Parish") != -1) x.substring(0, x.indexOf("Parish") - 1) else x)

    val state = crime_origin_dataset.select($"county_name").map(x => x.getString(0).split(",")(1).substring(1))

    val rate = crime_origin_dataset.select($"crime_rate_per_100000").map(x => x.getDouble(0))

    val county_iterator = county.collect.toList.iterator

    val state_iterator = state.collect.toList.iterator

    val rate_iterator = rate.collect.toList.iterator

    val county_state_rate_list = new ArrayBuffer[String]

    while (county_iterator.hasNext && state_iterator.hasNext && rate_iterator.hasNext) {
      county_state_rate_list += (county_iterator.next + """,""" + state_iterator.next + """,""" + rate_iterator.next.toString)
    }

    val county_state_rate = sc.parallelize(county_state_rate_list.toList).map(x => Seq(x.split(",")(0), x.split(",")(1), x.split(",")(2)))

    val cirmeDf = county_state_rate.map(x => X(x(0), x(1), x(2).toDouble)).toDF()

    val rentDf = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load("project/data/Zip_MedianListingPricePerSqft_AllHomes.csv")

    val selectedData = rentDf.select("RegionName", "City", "State", "CountyName", "2017-09")

    val df_rent = selectedData.as("dfrent")

    val df_crime = cirmeDf.as("dfcrime")

    val join_df = df_rent.join(df_crime, col("dfrent.CountyName") === col("dfcrime.County") && col("dfrent.State") === col("dfcrime.State"), "inner")
      .select("dfrent.RegionName", "dfrent.City", "dfrent.State", "dfrent.CountyName", "dfrent.2017-09", "dfcrime.Crime_Rate_Per_100000")
      .filter($"Crime_Rate_Per_100000" !== 0.0)

    join_df.write.save("project/data/JoinedData")
  }
}