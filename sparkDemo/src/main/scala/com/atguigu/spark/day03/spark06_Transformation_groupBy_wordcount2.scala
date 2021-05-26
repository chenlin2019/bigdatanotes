package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * groupBy（fun）  按照fun函数的返回值进行分组
 */
object spark06_Transformation_groupBy_wordcount2 {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3 创建一个RDD
    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("Hello Scala", 2), ("Hello Spark", 3), ("Hello World", 2)))

    // 方式一：
    rdd.map {
      case (word, count) => {
        (word + " ") * count
      }
    }.flatMap(_.split(" "))
      .groupBy(word => word)
      .map {
        case (word, list) => {
          (word, list.size)
        }
      }.collect().foreach(println)

    println("--------------------------------")
    //方式二
    rdd.flatMap{
      case (word,count)=>{
        word.split(" ")
          .map(word=>(word,count))
      }
    }.groupBy(word=>word._1).map{
      case (word,datas)=>{
        (word,datas.map(_._2).sum)
      }
    }.collect().foreach(println)


    //4.关闭连接
    sc.stop()
  }
}
