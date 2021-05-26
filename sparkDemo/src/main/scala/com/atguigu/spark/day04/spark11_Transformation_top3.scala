package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark11_Transformation_top3 {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3. 读取日志文件，获取原始数据
    val dataRDD: RDD[String] = sc.textFile("agent/agent.log")
    //数据准备：时间戳，省份，城市，用户，广告，中间字段使用空格分割。
    //1516609143867 6 7 64 16
    // 需求：统计出每个省广告点击top3
    dataRDD.map{
      line=>{
        val files: Array[String] = line.split(" ")
        (files(1)+"_"+files(4),1)
      }
    }.reduceByKey(_+_).map{
      case (prvAndAdv,clickCount)=>{
        val ks: Array[String] = prvAndAdv.split("_")
        (ks(0),(ks(1),clickCount))
      }
    }.groupByKey().mapValues{
          // sortWith集合排序不是算子
      datas=>datas.toList.sortWith(
        (left,right)=>
          left._2>right._2
      ).take(3)
    }.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

