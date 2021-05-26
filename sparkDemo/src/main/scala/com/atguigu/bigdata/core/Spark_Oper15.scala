package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
   reduceByKey
   1. 在一个(K,V)的RDD上调用，返回一个(K,V)的RDD，
   使用指定的reduce函数，将相同key的值聚合到一起，reduce任务的个数可以通过第二个可选的参数来设置。
* */
object Spark_Oper15 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)
    val listRDD: RDD[(String, Int)] = sc.makeRDD(List(("female", 1), ("male", 5), ("female", 5), ("male", 2)))

//    val reducelist: RDD[(String, Int)] = listRDD.reduceByKey(_ + _)
    val reducelist: RDD[(String, Int)] = listRDD.reduceByKey((x,y)=>x+y)

    reducelist.collect().foreach(println)
  }

}
