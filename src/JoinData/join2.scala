// start spark shell by using following command: spark-shell --master local --packages com.databricks:spark-csv_2.10:1.5.0

import org.apache.spark.sql.SQLContext

object Join2 {
  def main(args: Array[String]): Unit = {

    val sqlContext = new SQLContext(sc)

    val crime_price_dataset = sqlContext.read
      .load("project/data/JoinedData/*")

    val people_dataset = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("project/data/demographic.csv")


    val df_price = crime_price_dataset.as("dfprice")

    val df_people = people_dataset.as("dfpeople")

    val join_df = df_price.join(df_people, col("dfprice.CountyName") === col("dfpeople.county") && col("dfprice.State") === col("dfpeople.state"), "inner")
      .select("dfprice.RegionName", "dfprice.City", "dfprice.State", "dfprice.CountyName", "dfprice.2017-09", "dfprice.Crime_Rate_Per_100000",
        "dfpeople.male", "dfpeople.female", "dfpeople.white", "dfpeople.black", "dfpeople.asian", "dfpeople.hispanic")


    join_df.write.save("project/data/JoinedData2")
  }
}