package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}



object SparkSQL01_Demo01 {
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    // 读取数据，构建dataframe
    val frame: DataFrame = spark.read.json("in/user.json")

    // 展示数据
    frame.show()

    // 释放资源
    spark.stop()
  }
}
