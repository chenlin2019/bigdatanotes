package com.atguigu.spark.day11

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming02_State_updateStateByKey {
  def main(args: Array[String]): Unit = {
    //创建SparkConf
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建StreamingContext
    val ssc = new StreamingContext(conf, Seconds(3))


    //设置检查点路径  用于保存状态
    ssc.checkpoint("D:\\java_study_notes\\sparkDemo\\CheckpointDir")

    //创建DStream
    val lineDStream: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop202", 9999)
    //扁平映射
    val flatMapDS: DStream[String] = lineDStream.flatMap(_.split(" "))
    //结构转换
    val mapDS: DStream[(String, Int)] = flatMapDS.map((_,1))
    //聚合
    // 注意：DStreasm中reduceByKey只能对当前采集周期（窗口）进行聚合操作，没有状态
    //val reduceDS: DStream[(String, Int)] = mapDS.reduceByKey(_+_)


    /**
     * (hello,1)(hello,1)(hello,1)-->hello-->(1,1,1)
     */
    val stateDS: DStream[(String, Int)] = mapDS.updateStateByKey(
      /**
       * 第一个参数：表示的当前相同的key的value的数据集合(1,1,1)
       * 第二个参数：表示相同的key缓冲区的数据集合
       */
      (seq: Seq[Int], state: Option[Int]) => {
        /**
         * seq.sum：当前key对应的value进行求和
         * state.getOrElse(0)：获取缓冲区数据
         */
        Option(seq.sum + state.getOrElse(0))
      }
    )
    //打印输出
    stateDS.print()
    //启动
    ssc.start()
    ssc.awaitTermination()
  }
}
