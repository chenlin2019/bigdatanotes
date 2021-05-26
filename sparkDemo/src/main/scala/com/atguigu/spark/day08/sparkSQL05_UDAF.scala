package com.atguigu.spark.day08

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, IntegerType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
 * 弱类型实现UDAF 输入多行,返回一行
 */
object sparkSQL_UDAF {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val df: DataFrame = spark.read.json("D:\\java_study_notes\\sparkDemo\\in\\user.json")

    val myAvg: myAvg = new myAvg
    spark.udf.register("myAvg",myAvg)


    df.createOrReplaceTempView("user")
    spark.sql("select myAvg(age) from user").show()


    // 释放资源
    spark.stop()
  }
}

//自定义弱类型UDAF函数
class myAvg extends UserDefinedAggregateFunction{
  // 聚合函数输入数据的类型
  override def inputSchema: StructType = {
    StructType(Array(StructField("age",IntegerType)))
  }

  // 缓存数据类型
  override def bufferSchema: StructType = {
    StructType(Array(StructField("sum",LongType),StructField("count",LongType)))
  }

  // 聚合函数返回的数据类型，平均值只有一个输出所以只用一个类型
  override def dataType: DataType = DoubleType

  // 稳定性，默认不处理，直接返回true， 相同输入是否得到相同输出
  override def deterministic: Boolean = true

  // 初始化，缓存设置到初始状态
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    // 让缓存中年龄总和归0
    buffer(0)=0L
    // 让缓存中人数总和归0
    buffer(1)=0L
  }

  /**
   * 更新缓存数据
   * @param buffer 缓存数据
   * @param input 输入的数据
   */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if(!buffer.isNullAt(0)){
      buffer(0)=buffer.getLong(0)+input.getInt(0)
      buffer(1)=buffer.getLong(1)+1L
    }

  }

  // 分区间 合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0)+buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1)+buffer2.getLong(1)
  }

  // 计算逻辑
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble/buffer.getLong(1)
  }
}
