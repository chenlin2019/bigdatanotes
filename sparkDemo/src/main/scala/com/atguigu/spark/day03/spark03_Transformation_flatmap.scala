package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * mapPartitionsWithIndex（分区编号，分区数据）  可以对某个分区进行操作
 */
object spark03_Transformation_flatmap {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val listRDD: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7,8)), 2)

    val newRDD: RDD[Int] = listRDD.flatMap(datas => datas)

    newRDD.collect().foreach(println)

    println("===================================")

    val listRDD2: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7,8)), 2)

    val newRDD2 = listRDD2.map(datas => datas)
    newRDD2.collect().foreach(println)
    //4.关闭连接
    sc.stop()
  }
}
