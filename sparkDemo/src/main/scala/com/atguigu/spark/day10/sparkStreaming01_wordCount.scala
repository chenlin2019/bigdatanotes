package com.atguigu.spark.day10

import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming01_wordCount {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称,注意：Streaming程序至少需要两个线程，所以不能设置为local
    val conf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")

    //创建SparkStreaming执行的入口点对象
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))


    // 从指定端口获取
    val socketDS: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 9999)
    val flatMapDS: DStream[String] = socketDS.flatMap(_.split(" "))
    val mapDS: DStream[(String, Int)] = flatMapDS.map((_, 1))
    val reduceByKeyDS: DStream[(String, Int)] = mapDS.reduceByKey(_ + _)
    // 打印输出
    reduceByKeyDS.print()


    // 启动采集器
    ssc.start()
    // Drvier 等待采集器的执行
    ssc.awaitTermination()
  }
}
