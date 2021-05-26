package com.atguigu.bigdata.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 基础的流式
 */
object sparkStreaming01_wordcount {
  def main(args: Array[String]): Unit = {
    // 使用sparkstreaming完成wordcount

    // spark配置对象
    val sparkconfig = new SparkConf().setMaster("local[*]").setAppName("sparkStreaming01_wordcount")


    // 实时数据分析环境对象
    // 采集周期，以指定的时间为周期采集实时数据,设定3秒采集周期
    val streamingcontext: StreamingContext = new StreamingContext(sparkconfig, Seconds(3))

    // 从指定的端口中采集数据,端口上获取的一行一行的数据
    val socketLineDStream: ReceiverInputDStream[String] = streamingcontext.socketTextStream("hadoop101", 9999)

    // 将采集的数据进行分解（扁平化）
    val wordDStream: DStream[String] = socketLineDStream.flatMap(line => line.split(" "))

    // 将数据进行结构的转换方便统计分析
    val mapDStream: DStream[(String, Int)] = wordDStream.map((_, 1))

    // 将转换后的结构进行聚合处理
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
