package com.atguigu.spark.day10

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object sparkStreaming02_RDDQueue {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称,注意：Streaming程序至少需要两个线程，所以不能设置为local
    val conf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")

    //创建SparkStreaming执行的入口点对象
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))

    // 创建RDD队列
    val rddQueue = new mutable.Queue[RDD[Int]]()
    // 创建QueueInputDStream,从队列中采集数据
    val inputStream = ssc.queueStream(rddQueue,oneAtATime = false)
    // 处理队列中的RDD数据
    val mappedStream = inputStream.map((_,1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    // 打印结果
    reducedStream.print()
    // 启动任务
    ssc.start()
    // 循环创建并向RDD队列中放入RDD
    for (i <- 1 to 5) {
      rddQueue += ssc.sparkContext.makeRDD(6 to 10, 10)
      Thread.sleep(2000)
    }
    ssc.awaitTermination()



    // 释放资源
    ssc.stop()
  }
}
