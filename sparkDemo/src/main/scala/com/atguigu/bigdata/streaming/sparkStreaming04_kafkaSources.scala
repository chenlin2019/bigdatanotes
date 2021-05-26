package com.atguigu.bigdata.streaming

import java.io.{BufferedReader, InputStreamReader}
import java.net
import java.nio.charset.StandardCharsets
import java.net.Socket

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.tools.nsc.io.Socket

/**
 * kafka :
 * 开启生产者模拟生成数据：
 * kafka-console-producer.sh \
 * --broker-list hadoop101:9092,hadoop102:9092,hadoop103:9092 \
 * --topic atguigu
 *
 * 参数解析：
 * --broker-list  指定kafka节点信息
 * --broker-list 指定broker的列表
 * --topic 指定topic的名字
 */
object sparkStreaming04_kafkaSources {
  def main(args: Array[String]): Unit = {
    // 使用sparkstreaming完成wordcount

    // spark配置对象
    val sparkconfig = new SparkConf().setMaster("local[*]").setAppName("sparkStreaming01_wordcount")


    // 实时数据分析环境对象
    // 采集周期，以指定的时间为周期采集实时数据,设定3秒采集周期
    val streamingcontext: StreamingContext = new StreamingContext(sparkconfig, Seconds(3))

    //2.定义kafka参数
    val zkQuorum = "hadoop101:2181,hadoop102:2181,hadoop103:2181" // zk集群信息
    val groupId = "atguigu_spark_kafka"  // 消费组
    val topic = "atguigu"

    // 通过KafkaUtil创建kafkaDSteam
    val kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(
      streamingcontext,
      zkQuorum,
      groupId,
      Map(topic -> 3) // 分组的数量进行映射关联

    )

    // 将采集的数据进行分解（扁平化）,kafka的消息是k-v，key默认是null，所以只针对v进行操作
    val wordDStream: DStream[String] = kafkaDStream.flatMap(t=>t._2.split(" "))

    // 将数据进行结构的转换方便统计分析
    val mapDStream: DStream[(String, Int)] = wordDStream.map((_, 1))

    // 将转换后的结构进行聚合处理,在内存中做聚合
    val wordToSumStream: DStream[(String, Int)] = mapDStream.reduceByKey(_ + _)

    // 将结果打印出来
    wordToSumStream.print()

    // 不能停止采集程序
    //streamingcontext.stop()

    // 启动采集器
    streamingcontext.start()
    // Drvier 等待采集器的执行
    streamingcontext.awaitTermination()


  }
}

