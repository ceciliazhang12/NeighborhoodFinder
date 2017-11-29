// run it with: spark-shell --packages com.databricks:spark-csv_2.10:1.5.0

import org.apache.spark.sql.SQLContext

val sqlContext = new SQLContext(sc)
val df = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true") // Use first line of all files as header
    .option("inferSchema", "true") // Automatically infer data types
    .load("project/data/Zip_newPrice.csv")


val ETLData = df.withColumn("RegionName", when($"RegionName".isNull, 0))
.withColumn("City", when($"City".isNull, "none"))
.withColumn("2017-09", when($"2017-09".isNull, -1))