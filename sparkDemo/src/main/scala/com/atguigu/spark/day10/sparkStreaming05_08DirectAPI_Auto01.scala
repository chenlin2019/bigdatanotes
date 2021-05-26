package com.atguigu.spark.day10

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 自动维护偏移量在checkpoint中
 * 目前版本只是把offset放在检查点中，并没有从检查点中取，会存在消息丢失
 */
object sparkStreaming05_08DirectAPI_Auto01 {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称,注意：Streaming程序至少需要两个线程，所以不能设置为local
    val conf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")

    //创建SparkStreaming执行的入口点对象
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))

    ssc.checkpoint("D:\\java_study_notes\\sparkDemo\\CheckpointDir")

    //装备kafka参数
    val kafkaParams: Map[String, String] = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "bigdata"
    )

    val kafkaDstream: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](
      ssc,
      kafkaParams,
      // 消费主题
      Set("bigdata-0105")
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
