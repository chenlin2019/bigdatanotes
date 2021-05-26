package com.atguigu.spark.day05

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark07_Lineage {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello hadoop"))
    //查看rdd的血缘关系
    println(rdd.toDebugString)
    println("-----------------------------------------------------")


    val flatmapRDD: RDD[String] = rdd.flatMap(_.split(" "))
    println(flatmapRDD.toDebugString)
    println("-----------------------------------------------------")



    val mapRDD: RDD[(String, Int)] = flatmapRDD.map((_, 1))
    println(mapRDD.toDebugString)
    println("-----------------------------------------------------")

    val reducebykeyRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _)
    println(reducebykeyRDD.toDebugString)
    println("-----------------------------------------------------")



    //4.关闭连接
    sc.stop()
  }
}
