package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}


object SparkSQL05_sql_UDAF{
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    // 进行转换之前，需要引入隐式转换规则
    // 这里的spark不是包的含义，是sparksession对象的名字
    import spark.implicits._


    // 创建集合函数对象
    val udaf = new MyAgeAvgFunction
    //注册聚合函数 ,给自定义函数对象命名
    spark.udf.register("avgAge",udaf)

    // 使用聚合函数
    val frame: DataFrame = spark.read.json("in/user.json")
    frame.createOrReplaceTempView("user")
    spark.sql("select avgAge(age) from user").show


    // 释放资源
    spark.stop()
  }
}

// 弱类型用户自定义聚合函数
// 1) 继承UserDefinedAggregateFunction
// 2）实现方法
class MyAgeAvgFunction extends UserDefinedAggregateFunction {

  // 函数输入的数据结构
  override def inputSchema: StructType = {
    new StructType().add("age",LongType)
  }

  // 计算时的数据结构
  override def bufferSchema: StructType = {
    new StructType().add("sum",LongType).add("count",LongType)
  }

  // 函数 返回的数据类型
  override def dataType: DataType = DoubleType

  // 函数是否稳定
  override def deterministic: Boolean = true

  //  计算之前缓冲区的初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0)=0L  // 0 表示第一个结构 也就是 sum
    buffer(1)=0L  // 1 表示第二个结构 也就是 count
  }

  // 根据查询结果更新缓冲区数据
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0)=buffer.getLong(0)+input.getLong(0)
    buffer(1)=buffer.getLong(1)+1   // 这是每次来加一的数
  }

  // 将多个节点的缓冲区合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    // sum
    buffer1(0)=buffer1.getLong(0)+buffer2.getLong(0)
    // ccount
    buffer1(1)=buffer1.getLong(1)+buffer2.getLong(1)
  }

  // 计算
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble/buffer.getLong(1)
  }
}
