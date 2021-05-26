package com.atguigu.spark.day11

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming03_State_window {
  def main(args: Array[String]): Unit = {
    //创建SparkConf
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    //设置检查点路径  用于保存状态
    ssc.checkpoint("D:\\java_study_notes\\sparkDemo\\CheckpointDir")
    //创建DStream
    val lineDStream: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop202", 9999)
    //扁平映射
    val flatMapDS: DStream[String] = lineDStream.flatMap(_.split(" "))
    //设置窗口大小，滑动的步长
    val windowDS: DStream[String] = flatMapDS.window(Seconds(6),Seconds(3))



    //结构转换
    val mapDS: DStream[(String, Int)] = windowDS.map((_,1))
    //聚合
    val reduceDS: DStream[(String, Int)] = mapDS.reduceByKey(_+_)
    reduceDS.print()
    //启动
    ssc.start()
    ssc.awaitTermination()
  }
}
