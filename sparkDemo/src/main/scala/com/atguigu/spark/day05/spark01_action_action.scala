package com.atguigu.spark.day05

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark01_action_action {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[Int] = sc.makeRDD(List(4,3,2,1))

    // reduce 功能说明：f函数聚集RDD中的所有元素，先聚合分区内数据，再聚合分区间数据。
    val res = rdd.reduce(_+_)
    println(res)

    // collect在驱动程序中，以数组Array的形式返回数据集的所有元素。

    rdd.collect().foreach(println)
    // foreach(f)遍历RDD中每一个元素
    rdd.foreach(println)

    // count()返回RDD中元素个数
    println(rdd.count())

    // first()返回RDD中的第一个元素
    println(rdd.first())
    // take()返回由RDD前n个元素组成的数组
    println(rdd.take(3).mkString(","))

    //takeOrdered()返回该RDD排序后前n个元素组成的数组
    println(rdd.takeOrdered(3).mkString(","))















    //4.关闭连接
    sc.stop()
  }
}
