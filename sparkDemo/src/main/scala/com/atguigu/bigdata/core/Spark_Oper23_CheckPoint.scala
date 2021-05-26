package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
无检查点
(4) ShuffledRDD[2] at reduceByKey at Spark_Oper23_CheckPoint.scala:21 []
 +-(4) MapPartitionsRDD[1] at map at Spark_Oper23_CheckPoint.scala:19 []
    |  ParallelCollectionRDD[0] at makeRDD at Spark_Oper23_CheckPoint.scala:17 []

有检查点
(4) ShuffledRDD[2] at reduceByKey at Spark_Oper23_CheckPoint.scala:28 []
 +-(4) MapPartitionsRDD[1] at map at Spark_Oper23_CheckPoint.scala:23 []
    |  ParallelCollectionRDD[0] at makeRDD at Spark_Oper23_CheckPoint.scala:21 []

//  检查点
    (4) ShuffledRDD[2] at reduceByKey at Spark_Oper23_CheckPoint.scala:37 []
 |  ReliableCheckpointRDD[3] at foreach at Spark_Oper23_CheckPoint.scala:41 []
* */
object Spark_Oper23_CheckPoint {
  def main(args: Array[String]): Unit = {

    //1.初始化spark配置信息并建立与spark的连接
    val confing: SparkConf = new SparkConf().setAppName("Practice").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(confing)

    // 设置检查单目录
    sc.setCheckpointDir("CheckpointDir")

    val listRDD = sc.makeRDD(List(1, 2, 3, 4))

    val mapRDD: RDD[(Int, Int)] = listRDD.map((_, 1))



    val reduceRDD: RDD[(Int, Int)] = mapRDD.reduceByKey(_ + _)
    //  检查点
    reduceRDD.checkpoint()

    reduceRDD.foreach(println)
    println(reduceRDD.toDebugString)


    sc.stop()
  }
}

