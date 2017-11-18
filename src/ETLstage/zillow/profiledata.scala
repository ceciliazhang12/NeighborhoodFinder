// run it with: spark-shell --packages com.databricks:spark-csv_2.10:1.5.0


import org.apache.spark.sql.SQLContext

val sqlContext = new SQLContext(sc)
val df = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true") // Use first line of all files as header
    .option("inferSchema", "true") // Automatically infer data types
    .load("project/data/Zip_MedianListingPricePerSqft_AllHomes.csv")

val selectedData = df.select("RegionName", "City", "2017-09")
selectedData.write
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .save("project/data/Zip_newPrice.csv")

selectedData.sort(selectedData("2017-09").asc).show
selectedData.sort(selectedData("2017-09").desc).show
selectedData.count
selectedData.printSchema