package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/*
* 需求：创建一个1-10数组的RDD，将所有元素*2形成新的RDD
* */
object Spark_Oper1 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 10)
    val mapRDD: RDD[Int] = listRDD.map(x => x * 2)

    mapRDD.collect().foreach(println)

  }
}
