package com.atguigu.spark.day10

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming04_08ReceiverAPI {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称,注意：Streaming程序至少需要两个线程，所以不能设置为local
    val conf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")

    //创建SparkStreaming执行的入口点对象
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))


    // 连接kafka，创建DStream
    // kafka发来的消息在 k-v的v中存储
    val kafkaDstream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(
      ssc,
      "hadoop102:2181,hadoop103:2181,hadoop104:2181",
      // 消费者组
      "bigdata",
      // 消费主题 主题名->分区数
      Map("bigdata-0105" -> 2)
    )

    // 获取kafka中的消息，我们只需要v的部分
    val lineDS: DStream[String] = kafkaDstream.map(_._2)
    val word: DStream[String] = lineDS.flatMap(_.split(" "))
    val wordToOneDStream: DStream[(String, Int)] = word.map((_, 1))
    val wordToCountDStream: DStream[(String, Int)] = wordToOneDStream.reduceByKey(_ + _)
    wordToCountDStream.print()

    //5.开启任务
    ssc.start()
    ssc.awaitTermination()
  }
}
