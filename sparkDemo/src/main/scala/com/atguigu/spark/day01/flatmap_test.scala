package com.atguigu.spark.day01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object flatmap_test {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val arrayRDD =sc.parallelize(Array("a_b","c_d","e_f"))
    arrayRDD.foreach(println) //打印结果1

    arrayRDD.map(string=>{
      string.split("_")
    }).foreach(x=>{
      println(x.mkString(",")) //打印结果2
    })

    arrayRDD.flatMap(string=>{
      string.split("_")
    }).foreach(x=>{
      println(x.mkString(","))//打印结果3
    })
    //4.关闭连接
    sc.stop()
  }
}
