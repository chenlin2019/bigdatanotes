package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark_RDD {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    // 创建RDD
    // 1）从内存中创建 makeRDD,第二个参数是设置两个分区90(不写为最大默认分区)
    val listRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5),2)
    // 2）从内存中创建 parallelize
    val arrayRDD: RDD[Int] = sc.parallelize(Array(1, 2, 3, 4),2)
    // 3）从外部存储中创建
    // 默认情况下，可以读取项目路径，也可以读取其他路径hdfs
    val fileRDD: RDD[String] = sc.textFile("in",3)



    listRDD.collect().foreach(println)
    arrayRDD.collect().foreach(println)
    fileRDD.collect().foreach(println)

    listRDD.saveAsTextFile("output")

  }
}
