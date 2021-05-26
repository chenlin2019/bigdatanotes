package com.atguigu.bigdata.sql


import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, TypedColumn}
import org.apache.spark.sql.expressions.Aggregator


object SparkSQL06_sql_UDAF {
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
    val udaf = new MyAgeAvgClassFunction


    // 将聚合函数转换为查询列
    val avgCol: TypedColumn[UserBean, Double] = udaf.toColumn.name("avgAge")

    val frame: DataFrame = spark.read.json("in/user.json")

    val userDs: Dataset[UserBean] = frame.as[UserBean]
    // 应用函数
    userDs.select(avgCol).show()
    // 释放资源
    spark.stop()
  }
}

case class UserBean(name: String, age: BigInt)

case class AvgBuffer(var sum: BigInt, var count: Int)

// 申明用户自定义聚合函数(强类型)
// 1) 继承Aggregator[传递进来的类型, 缓冲区做计算的类型, 返回的类型]
// 2）实现方法
class MyAgeAvgClassFunction extends Aggregator[UserBean, AvgBuffer, Double] {
  //初始化
  override def zero: AvgBuffer = {
    AvgBuffer(0, 0) // 缓冲区对象
  }

  // 聚合数据
  override def reduce(b: AvgBuffer, a: UserBean): AvgBuffer = {
    b.sum = b.sum + a.age
    b.count = b.count + 1
    b
  }

  //  缓冲区的合并操作
  override def merge(b1: AvgBuffer, b2: AvgBuffer): AvgBuffer = {
    b1.sum = b1.sum + b2.sum
    b1.count = b1.count + b2.count
    b1
  }

  // 完成计算
  override def finish(reduction: AvgBuffer): Double = {
    reduction.sum.toDouble / reduction.count
  }

  // 输入输出编码器的声明
  // 如果是自定义的类型 就用Encoders.product
  override def bufferEncoder: Encoder[AvgBuffer] = Encoders.product

  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble

}
