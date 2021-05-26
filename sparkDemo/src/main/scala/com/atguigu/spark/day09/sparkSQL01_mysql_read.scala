package com.atguigu.spark.day09

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object sparkSQL01_mysql_read {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._
    //方式1：通用的load方法读取
    spark.read.format("jdbc")
      .option("url", "jdbc:mysql://hadoop102:3306/test")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "000000")
      .option("dbtable", "user")
      .load().show


    //方式2:通用的load方法读取 参数另一种形式
    spark.read.format("jdbc")
      .options(Map("url"->"jdbc:mysql://hadoop102:3306/test?user=root&password=000000",
        "dbtable"->"user",
        "driver"->"com.mysql.jdbc.Driver")).load().show

    //方式3:使用jdbc方法读取
    val props: Properties = new Properties()
    props.setProperty("user", "root")
    props.setProperty("password", "000000")
    props.setProperty("driver", "com.mysql.jdbc.Driver")
    val df: DataFrame = spark.read.jdbc("jdbc:mysql://hadoop102:3306/test", "user", props)
    df.show

    // 释放资源
    spark.stop()
  }
}
