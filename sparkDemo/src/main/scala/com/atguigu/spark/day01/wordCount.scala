package com.atguigu.spark.day01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 */
object wordCount {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf = new SparkConf().setAppName("WC").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc = new SparkContext(conf)

    val value: RDD[(String, Int)] = sc.textFile("D:\\java_study_notes\\sparkDemo\\input").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)



    value.saveAsTextFile("output")
    value.foreach(println)

    //8.关闭连接
    sc.stop()
  }
}
