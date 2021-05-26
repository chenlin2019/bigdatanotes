package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * mapPartitionsWithIndex（分区编号，分区数据）  可以对某个分区进行操作
 */
object spark02_Transformation_mapPartitionsWithIndex {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)


    // 创建一个RDD，使每个元素跟所在分区号形成一个元组，组成一个新的RDD
    val newRDD: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((index, datas) => {
      datas.map((index, _))
    })
    newRDD.collect().foreach(println)


    //扩展功能：第二个分区元素*2，其余分区不变
    val newRDD2: RDD[Int] = rdd.mapPartitionsWithIndex {
      (index, datas) => {
      index match {
        case 1 => datas.map((_ * 2))
        case _ => datas
      }
    }
    }
    newRDD2.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
