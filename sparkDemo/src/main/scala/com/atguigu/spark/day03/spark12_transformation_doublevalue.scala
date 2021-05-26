package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * -并集
 *  union
 * -交集
 *  intersection
 * -差集
 *  subtract
 * -拉链
 *  zip
 *   分区中元素个数一致
 *   分区一致
 */
object spark12_transformation_doublevalue {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd1: RDD[Int] = sc.makeRDD(1 to 4,3)

    //3.2 创建第二个RDD
    val rdd2: RDD[Int] = sc.makeRDD(4 to 7,3)


    val unionRDD: RDD[Int] = rdd1.union(rdd2)
    val intersectionRDD: RDD[Int] = rdd1.intersection(rdd2)
    val subtractRDD: RDD[Int] = rdd1.subtract(rdd2)
    val zipRDD: RDD[(Int,Int)] = rdd1.zip(rdd2)


    unionRDD.collect().foreach(println)
    println("********************************************")
    intersectionRDD.collect().foreach(println)
    println("********************************************")
    subtractRDD.collect().foreach(println)
    println("********************************************")
    zipRDD.collect().foreach(println)


    //4.关闭连接
    sc.stop()
  }
}
