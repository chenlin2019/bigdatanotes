package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*

* */
object Spark_Oper12 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 16)

    val sortByRDD: RDD[Int] = listRDD.sortBy(x => x)  // 默认升序
    val sortByRDD1: RDD[Int] = listRDD.sortBy(x => x % 2)  //  按余数升序

    val sortByRDD2: RDD[Int] = listRDD.sortBy(x => x % 2,false) // true正序，false倒叙

    val sortByRDD3: RDD[Int] = listRDD.sortBy(x => x % 2,false,4) // 分区提升效率


    sortByRDD.collect().foreach(println)
    sortByRDD1.collect().foreach(println)
    sortByRDD2.collect().foreach(println)
    sortByRDD3.collect().foreach(println)
  }
}
