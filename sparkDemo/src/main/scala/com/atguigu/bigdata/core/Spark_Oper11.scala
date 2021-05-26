package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    repartition(numPartitions) 案例
    1. 作用：根据分区数，重新通过网络随机洗牌所有数据。
* */
object Spark_Oper11 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 16,4)
    println(listRDD.partitions.size)
    listRDD.saveAsTextFile("output1")

    // 缩减分区,可以简单的理解为合并分区，默认suff
    val repartitionRDD: RDD[Int] = listRDD.repartition(2)
    println(repartitionRDD.partitions.size)

    repartitionRDD.collect().foreach(println)
    repartitionRDD.saveAsTextFile("output2")
  }
}
