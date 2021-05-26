package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    join: 需求：创建两个pairRDD，并将key相同的数据聚合到一个元组。
* */
object Spark_Oper21 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD1: RDD[(Int, String)] = sc.makeRDD(Array((1,"a"),(2,"b"),(3,"c")))
    val listRDD2: RDD[(Int, Int)] = sc.makeRDD(Array((1,4),(2,5),(3,6)))


    val joinList: RDD[(Int, (String, Int))] = listRDD1.join(listRDD2)

    joinList.collect().foreach(println)
  }
}
