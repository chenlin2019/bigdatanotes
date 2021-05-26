package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*

* */
object Spark_Oper10 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 16,4)
    println(listRDD.partitions.size)
    listRDD.saveAsTextFile("output1")

    // 缩减分区,可以简单的理解为合并分区，没有suff
    val coalesceRDD: RDD[Int] = listRDD.coalesce(3)
    // 可以设置suffle
//    val coalesceRDD: RDD[Int] = listRDD.coalesce(3,true)
    println(coalesceRDD.partitions.size)

    coalesceRDD.collect().foreach(println)
    coalesceRDD.saveAsTextFile("output2")
  }
}
