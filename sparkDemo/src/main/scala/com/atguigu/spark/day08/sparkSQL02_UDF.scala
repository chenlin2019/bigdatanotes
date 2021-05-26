package com.atguigu.spark.day08

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, SparkSession}

object sparkSQL02_UDF {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val df: DataFrame = spark.read.json("D:\\java_study_notes\\sparkDemo\\in\\user.json")

    // 注册自定义函数
    spark.udf.register("addSayHi",(name:String)=>("nihao:"+name))

    // 创建临时视图
    df.createOrReplaceTempView("user")
    spark.sql("select addSayHi(name) from user").show()

    // 释放资源
    spark.stop()
  }
}