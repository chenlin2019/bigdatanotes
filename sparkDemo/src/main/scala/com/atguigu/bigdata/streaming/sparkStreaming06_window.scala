package com.atguigu.bigdata.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object sparkStreaming06_window {
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

    // window（窗口大小应该为采集周期的整数倍，窗口滑动的步长也是采集周期的整数倍）
    val windowDStream: DStream[(String, String)] = kafkaDStream.window(Seconds(9), Seconds(3))


    // 将采集的数据进行分解（扁平化）,kafka的消息是k-v，key默认是null，所以只针对v进行操作
    val wordDStream: DStream[String] = windowDStream.flatMap(t=>t._2.split(" "))

    // 将数据进行结构的转换方便统计分析
    val mapDStream: DStream[(String, Int)] = wordDStream.map((_, 1))

    // 将转换后的结构进行聚合处理
    /**
     * updateStateByKey(Seq[V], Option[S]):
     *  Seq[V] 是当前的数据，Option[S] 是buffer的数据
     *  Seq和Option进行交互，将数据在放回到Option（缓冲区当中）
     */
    val stateDStream: DStream[(String, Int)] = mapDStream.updateStateByKey {
      case (seq, buffer) => {
        val sum = buffer.getOrElse(0) + seq.sum
        Option(sum) // 方法要求返回一个Option
      }
    }
    // 将结果打印出来
    stateDStream.print()
    // 启动采集器
    streamingcontext.start()
    // Drvier 等待采集器的执行
    streamingcontext.awaitTermination()
  }
}

