package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    mapValues 案例 需求：创建一个pairRDD，并将value添加字符串"|||"
* */
object Spark_Oper20 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD: RDD[(Int, String)] = sc.makeRDD(Array((1,"a"),(1,"d"),(2,"b"),(3,"c")))

    val mapValuesList: RDD[(Int, String)] = listRDD.mapValues(_ + "|||")

    mapValuesList.collect().foreach(println)
  }
}
