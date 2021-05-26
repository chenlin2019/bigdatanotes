package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, Partitioner, SparkConf, SparkContext}

/*
   自定义分区
* */
object Spark_Oper14 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)
    val listRDD: RDD[String] = sc.makeRDD(List("one", "two", "two", "three", "three", "three"))

    val listMap: RDD[(String, Int)] = listRDD.map(x => (x, 1))
    val groupList = listMap.groupByKey()

    groupList.collect().foreach(println)

    val sumlist: RDD[(String, Int)] = groupList.map(t => (t._1, t._2.sum))

    sumlist.collect().foreach(println)
  }

}
