package com.atguigu.bigdata.core

import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}


object Spark_Oper28_Var {
  def main(args: Array[String]): Unit = {

    //  1.创建spark配置信息
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("JdbcRDD")

    //  2.创建SparkContext
    val sc = new SparkContext(sparkConf)

    val rdd1: RDD[(Int, String)] = sc.makeRDD(List((1, "a"), (2, "b"), (3, "c")))
    val list: RDD[(Int, Int)] = sc.makeRDD(List((1, 1), (2, 2), (3, 3)))

    // 可以使用广播变量减少数据的传输
    // 1、构建广播变量
    val broadcast: Broadcast[RDD[(Int, Int)]] = sc.broadcast(list)

    val resultRDD: RDD[(Int, (String, Any))] = rdd1.map {
      case (key, value) => {
        var v2: Any = null
        for (t <- broadcast.value) {
          if (key == t._1) {
            v2 = t._2
          }
        }
        (key, (value, v2))
      }
    }
    resultRDD.foreach(println)
    sc.stop()
  }
}
