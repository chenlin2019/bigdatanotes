package com.atguigu.bigdata.streaming

import java.io.{BufferedReader, InputStreamReader}
import java.net
import java.nio.charset.StandardCharsets
import java.net.Socket
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.tools.nsc.io.Socket

/**
 * 基础的流式
 */
object sparkStreaming03_MyReceiver {
  def main(args: Array[String]): Unit = {
    // 使用sparkstreaming完成wordcount

    // spark配置对象
    val sparkconfig = new SparkConf().setMaster("local[*]").setAppName("sparkStreaming01_wordcount")


    // 实时数据分析环境对象
    // 采集周期，以指定的时间为周期采集实时数据,设定3秒采集周期
    val streamingcontext: StreamingContext = new StreamingContext(sparkconfig, Seconds(3))

    // 从指定的端口中采集数据,端口上获取的一行一行的数据,
    val receiverDstream = streamingcontext.receiverStream(new MyReceiver("hadoop101", 9999))

    // 将采集的数据进行分解（扁平化）
    val wordDStream: DStream[String] = receiverDstream.flatMap(line => line.split(" "))

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


//声明自定义采集器
class MyReceiver(host: String, port: Int) extends Receiver[String](StorageLevel.MEMORY_ONLY) {

  private var socket: java.net.Socket = null

  def receive(): Unit = {
    socket = new java.net.Socket(host, port)

    //创建一个BufferedReader用于读取端口传来的数据
    val reader = new BufferedReader(new InputStreamReader(socket.getInputStream, StandardCharsets.UTF_8))

    var line: String = null
    //读取数据
    while ((line = reader.readLine()) != null) {

      if ("END".equals(line)) {
        return
      } else {
        // 将采集的数据存储到采集器的内部进行转换
        this.store(line)
      }

    }
  }

  // 采集器怎么起动
  override def onStart(): Unit = {
    // 启动一个线程
    new Thread("Socket Receiver") {
      override def run() {
        receive()
      }
    }.start()
  }

  // 采集器怎么停止
  override def onStop(): Unit = {
    if (socket != null) {
      socket.close()
      socket = null
    }
  }
}
