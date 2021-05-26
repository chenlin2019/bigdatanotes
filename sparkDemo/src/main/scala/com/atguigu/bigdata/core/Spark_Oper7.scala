package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
glom()
* 作用：将每一个分区形成一个数组，形成新的RDD类型时RDD[Array[T]]
* */
object Spark_Oper7 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4,5,6,7,8))

    // 过滤出条件的数
    val filterRDD: RDD[Int] = listRDD.filter(x => x % 2 == 0)

    // 使用mkString方法来打印一个集合内容,用符号隔开
    filterRDD.collect().foreach(println)

  }
}
