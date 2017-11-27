val raw = sc.textFile("cc-est2016-alldata.csv")
val byCounty = raw.map(line => parseLine(line)).filter(t => t._1._1 != "Error")
val sumByCounty = byCounty.reduceByKey((x, y) => x.zip(y).map(x => x._1 + x._2))
val percByCounty = sumByCounty.mapValues(arr => arr.map(x => x.toFloat / arr(1)).takeRight(6))
val popByCounty = sumByCounty.mapValues(arr => arr(1))

val byAge = byCounty.map(t => ((t._1, t._2(0)), t._2(1))).reduceByKey((x, y) => x + y)
val ageByCounty = byAge.map(t => reshape(t)).reduceByKey((x, y) => x.zip(y).map(x => x._1 + x._2))
val agePerc = ageByCounty.join(popByCounty).mapValues(t => t._1.map(x => x.toFloat / t._2))

val res = percByCounty.join(popByCounty).join(agePerc)
val output = res.map(t => t._1._1 + "," + t._1._2 + "," + t._2._1._1.mkString(",") + "," + t._2._1._2 + "," + t._2._2.mkString(","))
val colNames = Array("county" ,"state", "male", "female", "white", "black", "asian", "hispanic", "total_population", "young", "mid_age", "senior")
val header = sc.parallelize(Array(colNames.mkString(",")))
val unioned = header.union(output)
unioned.coalesce(1).saveAsTextFile("demographic.csv")


def parseLine(line: String): ((String, String), Array[Int]) = {
    try {
        val data = line.split(",")
        val state = data(3)
        val county = data(4).replace(" County", "")
        // val year = if (data(5) == "13") 2010 else -1
        val AGEGRP = data(6).toInt
        val age_group = if (AGEGRP <= 4) 0 else if (AGEGRP <= 10) 1 else 2
        val population = data(7).toInt
        val male = data(8).toInt
        val female = data(9).toInt
        val white = data(10).toInt + data(11).toInt
        val black = data(12).toInt + data(13).toInt
        val asian = data(14).toInt + data(15).toInt
        val hispanic = data(56).toInt + data(57).toInt
        ((county, state), Array(age_group, population, male, female, white, black, asian, hispanic))
    } catch {
        case _: Throwable => (("Error", ""), Array())
    }
}

def reshape(t: (((String, String), Int), Int)): ((String, String), Array[Int]) = {
    val key = t._1._1
    val grp = t._1._2
    val arr = Array(0, 0, 0)
    arr(grp) = t._2
    (key, arr)
}