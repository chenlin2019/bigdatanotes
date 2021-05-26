package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object test {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(Array("a", "b", "c", "d"), 2)

    val glomRDD: RDD[Array[String]] = rdd.glom()

    val newRDD: RDD[String] = glomRDD.map {
      array => {
        array.fold("")(_ + _)
      }
    }


    newRDD.collect().foreach(println)


    //4.关闭连接
    sc.stop()
  }
}
