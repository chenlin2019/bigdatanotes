package com.atguigu.spark.day08

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object spark01_DF {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val df: DataFrame = spark.read.json("D:\\java_study_notes\\sparkDemo\\in\\user.json")

    //查看df数据
    df.show()

    // sql 风格
    df.createOrReplaceTempView("users")
    spark.sql("select * from users").show()

    //DSL风格
    df.select("name","age").show()
  }
}
