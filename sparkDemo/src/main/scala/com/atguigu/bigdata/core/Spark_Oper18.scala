package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    combineByKey[C] 案例
* */
object Spark_Oper18 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD: RDD[(String, Int)] = sc.makeRDD(Array(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98)), 2)

    listRDD.glom().collect().foreach(array=>println(array.mkString(",")))

    val combineRDD: RDD[(String, (Int, Int))] = listRDD.combineByKey(
      (_, 1),     //  初始值做转换，把v 变化成 （v，1）例如：（88,1）
      (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),         // 分区里做什么计算 （（88,1），20）
      (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2))   // 分区间做什么计算

    combineRDD.collect().foreach(println)


    val result: RDD[(String, Double)] = combineRDD.map {
      case (key, value) => (key, value._1 / value._2.toDouble)
    }

    result.collect().foreach(println)
  }
}
