package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * mapPartitionsWithIndex（分区编号，分区数据）  可以对某个分区进行操作
 */
object spark04_Transformation_glom {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3 创建一个RDD
    val rdd = sc.makeRDD(1 to 6, 2)

    val newRDD: RDD[Array[Int]] = rdd.glom()
    newRDD.mapPartitionsWithIndex {
      (index, datas) => {
        println(index + "----->" + datas.next().mkString(","))
      datas
      }
    }.collect()

    // 3.2 求出每个分区的最大值  0->1,2,3   1->4,5,6
    val maxRdd: RDD[Int] = rdd.glom().map(_.max)
    maxRdd.collect().foreach(println)

    // 3.3 求出所有分区的最大值的和
    println(maxRdd.collect().sum)
    //4.关闭连接
    sc.stop()
  }
}
