package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark05_Transformation_foldByKey {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3.1 创建第一个RDD
    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 3), ("a", 2), ("c", 4), ("b", 3), ("c", 6), ("c", 8)), 2)
    rdd.mapPartitionsWithIndex((index,datas)=>{
      println(index+"--------------->"+datas.mkString(","))
      datas
    })collect()

    /**
     * 分区内和分区间计算规则相同
     * foldByKey(初始值)(分区内计算规则和分区间计算规则)
     *  -(zeroValue: 初始值)
     *  -func ：分区内计算规则和分区间计算规则
     */

    //3.2 取出每个分区相同key对应值的最大值，然后分区间可以相同的值相加
    // 一下三种聚合方法：
    rdd.foldByKey(0)(_+_).collect().foreach(println)
    rdd.reduceByKey(_+_).collect().foreach(println)
    rdd.aggregateByKey(0)(_+_,_+_).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

