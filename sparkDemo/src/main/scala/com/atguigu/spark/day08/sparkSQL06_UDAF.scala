package com.atguigu.spark.day08

import com.atguigu.bigdata.sql.UserBean
import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, IntegerType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, TypedColumn}

/**
 * 强类型实现UDAF 输入多行,返回一行 ,主要应用在DSL风格的DS查询
 */
object sparkSQL06_UDAF {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val df: DataFrame = spark.read.json("D:\\java_study_notes\\sparkDemo\\in\\user.json")

    //TODO:注意如果是自定义UDAF的强类型，没有办法应用SQL风格DF的查询

    //创建自定义函数对象
    val myAvgNew = new MyAvgNew

    import spark.implicits._
    // df-->ds
    val ds: Dataset[UserBean] = df.as[UserBean]

    // 将自定义函数对象转化为查询列
    val col: TypedColumn[UserBean, Double] = myAvgNew.toColumn

    ds.select(col).show()


    // 释放资源
    spark.stop()
  }
}

//输入类型的样例类
case class UserBean(name: String, age: Long)

// 缓存数据样例类
case class AvgBuffer(var sum: Long, var count: Long)

/**
 * 自定义强类型UDAF函数
 * IN :输入数据类型
 * BUF：缓存数据类型
 * OUT：输出结果数据类型
 */
class MyAvgNew extends Aggregator[UserBean, AvgBuffer, Double] {
  // 缓存数据初始化
  override def zero: AvgBuffer = {
    AvgBuffer(0L, 0L)
  }

  /**
   * 当前分区内数据进行聚合
   *
   * @param b
   * @param a
   * @return b
   */
  override def reduce(b: AvgBuffer, a: UserBean): AvgBuffer = {
    b.sum = b.sum + a.age
    b.count += 1
    b
  }

  /**
   * 分区间数据合并
   *
   * @param b1
   * @param b2
   * @return b1
   */
  override def merge(b1: AvgBuffer, b2: AvgBuffer): AvgBuffer = {
    b1.sum = b1.sum + b2.sum
    b1.count = b1.count + b2.count
    b1
  }

  /**
   * 返回计算结果
   *
   * @param reduction
   * @return
   */
  override def finish(reduction: AvgBuffer): Double = {
    reduction.sum.toDouble / reduction.count
  }

  /**
   * DataSet的编码以及解码器，用于进行序列化固定写法
   *
   * @return
   */
  override def bufferEncoder: Encoder[AvgBuffer] = Encoders.product

  // 根据 类型选择 这里 根据double选择scalaDouble
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}