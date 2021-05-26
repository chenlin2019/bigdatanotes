package com.atguigu.bigdata.core

import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.rdd.RDD
import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}


object Spark_Oper27_ACC {
  def main(args: Array[String]): Unit = {

    //  1.创建spark配置信息
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("JdbcRDD")

    //  2.创建SparkContext
    val sc = new SparkContext(sparkConf)
/*
    val listRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    // 创建累加器对象
    val accumulator: LongAccumulator = sc.longAccumulator
    listRDD.foreach {
      case i => accumulator.add(i)
    }
    println("sum=" + accumulator.value)
    */

    val listRDD: RDD[String] = sc.makeRDD(List("hadoop", "hbase", "hive", "shell","Scala"), 2)
    val wordaccumulator: WordAccumulator = new WordAccumulator
    sc.register(wordaccumulator)

    listRDD.foreach{
      case word => {
        wordaccumulator.add(word)
      }
    }
    println("sum="+wordaccumulator.value)

    sc.stop()
  }
}


// 申明累加器
// 1、继承accumulator
// 2、实现抽象方法
// AccumulatorV2[输入, 返回]
class WordAccumulator extends AccumulatorV2[String, util.ArrayList[String]] {

  private val list: util.ArrayList[String] = new util.ArrayList[String]()

  // 当前的累加器是否为初始化状态,list.isEmpty为空就是初始化状态
  override def isZero: Boolean = {
    list.isEmpty
  }

  //  复制累加器对象
  override def copy(): AccumulatorV2[String, util.ArrayList[String]] = {
    new WordAccumulator
  }

  // 重置累加器对象
  override def reset(): Unit = {
    list.clear()
  }

  // 向累加器中增加数据
  override def add(v: String): Unit = {
    if (v.contains("h"))
      list.add(v)
  }

  // 合并,就是其他累加器和当前累加器合并，就是说当前累加器的值加上其他累加器的值
  override def merge(other: AccumulatorV2[String, util.ArrayList[String]]): Unit = {

    list.addAll(other.value)
  }

  //  获取累加器的结果
  override def value: util.ArrayList[String] = list
}