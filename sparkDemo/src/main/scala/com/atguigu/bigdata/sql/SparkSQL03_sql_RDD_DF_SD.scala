package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}


object SparkSQL03_sql_RDD_DF_SD {
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    // 进行转换之前，需要引入隐式转换规则
    // 这里的spark不是包的含义，是sparksession对象的名字
    import spark.implicits._

    // 创建RDD
    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 20), (2, "lisi", 30), (3, "wangwu", 40)))
    // 转换为DF
    val df: DataFrame = rdd.toDF("id", "name", "age")
    df.show()
    // 转换为DS
    val ds: Dataset[User] = df.as[User]
    ds.show()

    // 转换为DF
    val df1: DataFrame = ds.toDF()
    df1.show()

    // 转换为RDD
    val rdd1: RDD[Row] = df1.rdd
    rdd1.foreach(row=>{
      println(row.getInt(0)) // 0为第几列
    })

    // 释放资源
    spark.stop()
  }
}

// 创建样例类,用于df转换ds
case class User(id:Int,name:String,age:Int)