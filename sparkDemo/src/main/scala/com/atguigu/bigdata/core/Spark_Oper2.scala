package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
* 需求：创建一个1-10数组的RDD，将所有元素*2形成新的RDD
* */
object Spark_Oper2 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 10)

    // mapPartitions 可以对一个RDD中所有的分区进行遍历
    // mapPartitions 效率优于map算子，减少了发送到执行器执行交互次数
    // 可能会出现内存溢出（OOM）
    val mapRDD: RDD[Int] = listRDD.mapPartitions(datas=>{
      datas.map(data=>data*2)
    })

    mapRDD.collect().foreach(println)

  }
}
