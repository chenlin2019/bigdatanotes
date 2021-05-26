package com.atguigu.spark.day11

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
// 使用sparksql处理采集周期中的数据
object sparkStreaming04_kafka_sparkSQL {
  def main(args: Array[String]): Unit = {
    //创建conf
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建StreamingContext
    val ssc = new StreamingContext(conf, Seconds(3))


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

    val resDS: DStream[(String, Int)] = kafkaDstream.transform {
      rdd =>
        val newRDD: RDD[(String, Int)] = rdd.map(_._2)
          .flatMap(_.split(" "))
          .map((_, 1)).reduceByKey(_ + _)
        newRDD
    }

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    /**
     * DStream -->DF
     */
    resDS.foreachRDD{
      rdd=>{
        //将RDD转换成DataFram
        val df: DataFrame = rdd.toDF("word", "count")
        df.createOrReplaceTempView("words")
        //执行sql
        spark.sql("select * from words ").show()
      }
    }


    //启动
    ssc.start()
    ssc.awaitTermination()
  }
}
