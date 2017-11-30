# NeighborhoodFinder
Living Area Recommendation System on Demand

## Members:
Oukan Fan

Xi Huang

Yanyu Zhang


## Background:
We are building a living area recommendation system that can output a list of neighborhoods ranking in United States for user according to his/her personal demands. User first answer couple of questions on our system, such as the population structure preference, affordable living cost range, crime rate, then our system will process the input, do the analytics and output the recommendation result for this user.

## Stack

### Technologies
- [Scala](https://www.scala-lang.org/) is the programming language we use to implement this spark application.
- [Spark](https://spark.apache.org/) is the powerful data processing engine we use to run application.

### Libraries
- [Spark MLlib](https://spark.apache.org/mllib/) is the package we use to perform clustering(K-means algorithm).

## Code Guide

### Data Join:
We have three raw datasets as following:
- the United States crime rate dataset (https://www.kaggle.com/mikejohnsonjr/united-states-crime-rates-by-county/data)
- demographic datasets (https://www2.census.gov/programs-surveys/popest/datasets/2010-2016/counties/asrh/)
- house-renting price dataset (https://www.zillow.com/research/data/) 

For further use, we need to join the three datasets into one data frame. 

`src/JoinData` is the main directory with two scala scripts performing data joining. 
  * join.scala combines the crime rate data and house-renting price dataset.
  * join2.scala combines intermediate dataset and demographic dataset.
  
Use `sbt package` to package these two scala scripts into JAR files respectively

Then, to run these two spark application, please use the following command:

`spark-submit --master yarn-cluster --class join join.jar [fileURL]`

`spark-submit --master yarn-cluster --class join2 join2.jar [fileURL]`

### Derive K-means clustering model:
Now that we have the final dataframe we have, we can get the model by training it in spark. 

`src\Kmeans` is the main directory with a script named kmeans.scala performing clustering and prediction.

Simply run the kmeans.scala file in the following way, which would output model into the directory:

`spark-submit --master yarn-cluster --class kmeans kmeans.jar [fileURL]`