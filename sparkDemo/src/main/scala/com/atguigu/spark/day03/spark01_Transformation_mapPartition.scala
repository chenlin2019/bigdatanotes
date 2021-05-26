package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark01_Transformation_mapPartition {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)


    // 一般适用于批处理的操作，比如：将rdd中的元素插入到数据库中，需要数据库连接，
    // 如果每个元素创建一次连接消耗很大
    val newRDD: RDD[Int] = rdd.mapPartitions(_.map(_ * 2))


    newRDD.collect().foreach(println)
    //4.关闭连接
    sc.stop()
  }
}
