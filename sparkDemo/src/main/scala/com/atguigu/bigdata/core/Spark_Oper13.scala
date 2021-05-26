package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, Partitioner, SparkConf, SparkContext}

/*
   自定义分区
* */
object Spark_Oper13 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)
    val listRDD: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 2), ("c", 3), ("d", 4)))
    val parRDD: RDD[(String, Int)] = listRDD.partitionBy(new MyParitioner(3))

    parRDD.saveAsTextFile("output")
  }
}

// 声明分区器
// 继承Partitioner类
class MyParitioner(partitions: Int) extends Partitioner {
  override def numPartitions: Int = {
    partitions
  }

  override def getPartition(key: Any): Int = {
    if (key == "a") {
      0
    } else if (key == "b") {
      1
    } else {
      2
    }

  }
}
