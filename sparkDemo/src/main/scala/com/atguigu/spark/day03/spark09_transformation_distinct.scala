package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark09_transformation_distinct {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3.1 创建一个RDD
    val numRdd: RDD[Int] = sc.makeRDD(List(1,2,1,5,2,9,6,1),5)
    numRdd.mapPartitionsWithIndex{
      (index,datas)=>{
      println(index+"----------->"+ datas.mkString(","))
        datas
      }
    }.collect()

  println("----------------------")

    /**
     * distinct(num)
     *  可以设置分区数
     */
    val disRDD: RDD[Int] = numRdd.distinct(2)
    disRDD.mapPartitionsWithIndex{
      (index,datas)=>{
        println(index+"----------->"+ datas.mkString(","))
        datas
      }
    }.collect()

    //4.关闭连接
    sc.stop()
  }
}
