package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * filter 按照指定的规则过滤
 */
object spark07_Transformation_filter {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3 创建一个RDD
    val rdd = sc.makeRDD(List("zhangsan","lisi","wangwu","xiaosan"), 2)

    val filterRDD: RDD[String] = rdd.filter(_.startsWith("xiao"))

    filterRDD.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
