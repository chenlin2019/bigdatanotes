package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * groupBy（fun）  按照fun函数的返回值进行分组
 */
object spark06_Transformation_groupBy_wordcount {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3 创建一个RDD
    val strList: RDD[String] = sc.makeRDD(List("Hello Scala", "Hello Spark", "Hello World"))

    val groupRDD: RDD[(String, Iterable[String])] = strList.flatMap(_.split(" ")).groupBy(word => word)

    val wordToSum: RDD[(String, Int)] = groupRDD.map {
      case (t1, t2) => (t1, t2.size)
    }


    wordToSum.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
