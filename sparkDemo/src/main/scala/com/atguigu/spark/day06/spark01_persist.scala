package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark01_persist {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))

    val flatmapRDD: RDD[String] = rdd.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Int)] = flatmapRDD.map {
      word =>
        println("****************************")
        (word, 1)
    }


    println(mapRDD.toDebugString)

    // 对rdd算子进行缓存,可接收参数指定缓存的位置
    mapRDD.persist()
    // 触发行动算子
    mapRDD.collect()
    println("-------------------------------")


    println(mapRDD.toDebugString)
    mapRDD.collect()
    println("-------------------------------")



    //4.关闭连接
    sc.stop()
  }
}
