package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark04_hdfs {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 读取hdfs文件
    val hdfsRDD: RDD[String] = sc.textFile("hdfs://hadoop102:9000/testHDFSputdatas")


    hdfsRDD.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
