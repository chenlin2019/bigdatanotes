package com.atguigu.spark.day02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 通过读取内存集合，创建rdd
 */
object spark01_CreateRDD_mem {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val list: List[Int] = List(1, 2, 3, 4)
    // 根据集合创建rdd，方式一
    val rdd1: RDD[Int] = sc.parallelize(list)
    // 根据集合创建rdd，方式二,makeRDD底层就是parallelize
    val rdd2: RDD[Int] = sc.makeRDD(list)


    rdd1.collect().foreach(println)
    rdd2.collect().foreach(println)
    //4.关闭连接
    sc.stop()
  }
}
