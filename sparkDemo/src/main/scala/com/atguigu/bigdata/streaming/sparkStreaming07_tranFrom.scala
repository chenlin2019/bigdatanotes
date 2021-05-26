package com.atguigu.bigdata.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object sparkStreaming07_tranFrom {
  def main(args: Array[String]): Unit = {
    //scala 中的窗口函数
    val ints: List[Int] = List(1, 2, 3, 4, 5, 6)

    val iterator: Iterator[List[Int]] = ints.sliding(3, 3)

    for (list <- iterator) {
      println(list.mkString(","))
    }



    /*****************************************************************/
    // spark配置对象
    val sparkconfig = new SparkConf().setMaster("local[*]").setAppName("sparkStreaming01_wordcount")

    // 实时数据分析环境对象
    // 采集周期，以指定的时间为周期采集实时数据,设定3秒采集周期
    val streamingcontext: StreamingContext = new StreamingContext(sparkconfig, Seconds(3))

    // 保存数据的状态，需要设定检查点路径
    streamingcontext.sparkContext.setCheckpointDir("cp")

    // 定义kafka参数
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

    // 启动采集器
    streamingcontext.start()
    // Drvier 等待采集器的执行
    streamingcontext.awaitTermination()
  }
}

