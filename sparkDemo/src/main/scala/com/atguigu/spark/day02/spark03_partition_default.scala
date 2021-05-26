package com.atguigu.spark.day02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark03_partition_default {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    val rdd2: RDD[String] = sc.textFile("D:\\java_study_notes\\sparkDemo\\input")

    // 集合中创建rdd，查看默认分区为：local的参数
    println(rdd.partitions.size)
    rdd.saveAsTextFile("D:\\java_study_notes\\sparkDemo\\output1")
    // 读取外部文件默认分区为：min（分配给应用的cpu的核数，2）
    println(rdd2.partitions.size)
    rdd2.saveAsTextFile("D:\\java_study_notes\\sparkDemo\\output2")


    //4.关闭连接
    sc.stop()
  }
}
