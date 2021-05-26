package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark02_CheckPoint {
  def main(args: Array[String]): Unit = {
    // 设置访问HDFS集群的用户名
    System.setProperty("HADOOP_USER_NAME","atguigu")


    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)


    //设置检查点目录在hdfs
    sc.setCheckpointDir("hdfs://hadoop102:9000/CheckpointDir")
    // 设置检查点目录在本地
    //sc.setCheckpointDir("./CheckpointDir")


    val rdd: RDD[String] = sc.textFile("D:\\java_study_notes\\sparkDemo\\in\\2.txt",2)
    val flatMapRDD: RDD[(String)] = rdd.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Long)] = flatMapRDD.map {
      word => {
        (word, System.currentTimeMillis())
      }
    }

    println(mapRDD.toDebugString)

    // TODO 在开发环境，一般检查点和缓存配合使用
    // 设置缓存
    mapRDD.cache()
    //设置检查点
    mapRDD.checkpoint()


    mapRDD.collect().foreach(println)

    // 释放缓存
    mapRDD.unpersist()
    println("--------------------------------------------")


    println(mapRDD.toDebugString)
    mapRDD.collect().foreach(println)





    //4.关闭连接
    sc.stop()
  }
}
