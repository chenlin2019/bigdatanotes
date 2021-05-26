package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}


object SparkSQL02_sql_read {
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    // 读取数据，构建dataframe
    val frame: DataFrame = spark.read.json("in/user.json")

    // 构建临时视图
    frame.createOrReplaceTempView("user")

    //采用sql的语法访问数据
    val frame1: DataFrame = spark.sql("select * from user")
    // 展示数据
    frame1.show()

    // 释放资源
    spark.stop()
  }
}
