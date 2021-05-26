package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

object spark06_Accumulator {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4), ("a", 5)))
    //系统累加器
    val sum: LongAccumulator = sc.longAccumulator("myAcc")

    val newRDD: Unit = rdd.foreach {
      case (word, count) => {
        sum.add(count)
      }
    }

    println(sum.value)
    //4.关闭连接
    sc.stop()
  }
}
