package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.parsing.json._

object spark03_json {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val jsonRDD: RDD[String] = sc.textFile("D:\\java_study_notes\\sparkDemo\\in\\user.json")
    //3.2 导入解析Json所需的包并解析Json

    val resultRDD: RDD[Option[Any]] = jsonRDD.map(JSON.parseFull)

    //3.3 打印结果
    resultRDD.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
