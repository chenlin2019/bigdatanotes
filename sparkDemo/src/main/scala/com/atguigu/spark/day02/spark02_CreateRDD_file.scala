package com.atguigu.spark.day02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark02_CreateRDD_file {

  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3.读取本地文件。
    val lineWordRdd: RDD[String] = sc.textFile("input")
    //3.读取文件。如果是集群路径：hdfs://hadoop102:9000/input
    val hdfsRdd: RDD[String] = sc.textFile("hdfs://hadoop102:9000/input")

    lineWordRdd.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }

}
