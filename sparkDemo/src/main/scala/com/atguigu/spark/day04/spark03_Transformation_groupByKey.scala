package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

object spark03_Transformation_groupByKey {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3.1 创建第一个RDD
    val rdd = sc.makeRDD(List(("a",1),("b",5),("a",5),("b",2)))


    val groupRDD: RDD[(String, Iterable[Int])] = rdd.groupByKey()
    groupRDD.collect().foreach(println)

    groupRDD.map{
      case(key,datas)=>{
        (key,datas.sum)
      }
    }.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

