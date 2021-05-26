package com.atguigu.spark.day10

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 自动维护偏移量在checkpoint中
 * 目前版本只是把offset放在检查点中，并没有从检查点中取，会存在消息丢失
 */
object sparkStreaming07_08DirectAPI_Hander {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称,注意：Streaming程序至少需要两个线程，所以不能设置为local
    val conf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")

    //创建SparkStreaming执行的入口点对象
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))


    //装备kafka参数
    val kafkaParams: Map[String, String] = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "bigdata"
    )
    //4.获取上一次消费的位置信息
    val fromOffsets: Map[TopicAndPartition, Long] = Map[TopicAndPartition, Long](
      TopicAndPartition("mybak", 0) -> 13L,
      TopicAndPartition("mybak", 1) -> 10L
    )
    // 获取上一次消费的位置（偏移量）
    // 实际项目中，为了保证数据精准一致，我们对数据进行消费处理之后 将偏移量保存在事务中存储，如mysql
    val kafakDStream: InputDStream[String] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, String](
      ssc,
      kafkaParams,
      fromOffsets,
      (m: MessageAndMetadata[String, String]) => m.message()
    )


    //6.定义空集合用于存放数据的offset
    var offsetRanges = Array.empty[OffsetRange]

    //7.将当前消费到的offset进行保存
    kafakDStream.transform { rdd =>
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }.foreachRDD { rdd =>
      for (o <- offsetRanges) {
        println(s"${o.topic}-${o.partition}-${o.fromOffset}-${o.untilOffset}")
      }
    }
    //5.开启任务
    ssc.start()
    ssc.awaitTermination()
  }
}
