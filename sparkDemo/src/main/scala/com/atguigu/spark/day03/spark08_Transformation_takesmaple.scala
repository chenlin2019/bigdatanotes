package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark08_Transformation_takesmaple {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val stds: List[String] = List("zhangsan", "lisi", "wangwu", "zhangliu","tianqi")

    val nameRDD: RDD[String] = sc.makeRDD(stds)

    /**
     * takeSample 指定个数采样
     */
    val newRDD: Array[String] = nameRDD.takeSample(false, 1)
    newRDD.foreach(println)


    //4.关闭连接
    sc.stop()
  }
}
