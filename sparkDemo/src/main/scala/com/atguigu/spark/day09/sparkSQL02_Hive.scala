package com.atguigu.spark.day09

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object sparkSQL02_Hive {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()


    spark.sql("show databases").show()


    // 释放资源
    spark.stop()
  }
}
