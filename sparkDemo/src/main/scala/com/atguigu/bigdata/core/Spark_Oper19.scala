package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    combineByKey[C] 案例
* */
object Spark_Oper19 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD: RDD[(Int, String)] = sc.makeRDD(Array((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")))

    val sortList: RDD[(Int, String)] = listRDD.sortByKey(false)  // true 为按key正序  ， false为按key倒叙


    sortList.collect().foreach(println)
  }
}
