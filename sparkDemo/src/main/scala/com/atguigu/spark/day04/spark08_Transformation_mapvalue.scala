package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark08_Transformation_mapvalue {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3.1 创建第一个RDD
    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "a"), (1, "d"), (2, "b"), (3, "c")))

    /**
     * 只对（key，value）中的value进行操作
     */
    rdd.mapValues(_ + "|||").collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

