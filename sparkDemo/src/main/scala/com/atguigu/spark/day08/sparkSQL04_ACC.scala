package com.atguigu.spark.day08

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.{AccumulatorV2, LongAccumulator}

object sparkSQL04_ACC {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val rdd: RDD[(String, Int)] = spark.sparkContext.makeRDD(List(("zhangsan", 20), ("lisi", 30)))

    val myACC = new myAccumulator1
    spark.sparkContext.register(myACC)

    rdd.foreach{
      case (name,age)=>{
        myACC.add(age)
      }
    }


    println(myACC.value)


    // 释放资源
    spark.stop()
  }
}


class myAccumulator1 extends AccumulatorV2[Int, Double] {

  var agesum: Int = 0
  var countsum: Int = 0

  override def isZero: Boolean = {
    agesum == 0 && countsum == 0
  }

  override def copy(): AccumulatorV2[Int, Double] = {
    val newAcc = new myAccumulator1
    newAcc.agesum = this.agesum
    newAcc.countsum = this.countsum
    newAcc

  }

  override def reset(): Unit = {
    agesum = 0
    countsum = 0
  }

  // 分区内累加
  // 累加器中添加元素，v是rdd中元素
  override def add(v: Int): Unit = {
    agesum = agesum + v
    countsum += 1

  }

  // 分区间累加
  override def merge(other: AccumulatorV2[Int, Double]): Unit = {
    other match {
      case mc:myAccumulator1=>{
        this.agesum +=mc.agesum
        this.countsum += mc.countsum
      }
      case _=>
    }

  }

  override def value: Double = {

    agesum.toDouble / countsum
  }
}
