package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, TypedColumn}
import org.apache.spark.sql.expressions.Aggregator


object SparkSQL07_sql_readAndsave {
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    // 进行转换之前，需要引入隐式转换规则
    // 这里的spark不是包的含义，是sparksession对象的名字
    import spark.implicits._



    // 释放资源
    spark.stop()
  }
}
