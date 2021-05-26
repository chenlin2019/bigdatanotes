package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark08_Transformation_smaple {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)


    val rdd: RDD[Int] = sc.makeRDD(1 to 9)

    /**
     * sample(
     * withReplacement: Boolean, 是否采样放回
     *  -true 抽样放回
     *  -false 抽样不放回
     *
     * fraction: Double, 根据第一个参数表示含义
     *  -withReplacement = true 抽样放回时，表示期望每一个元素出现的次数
     *  -withReplacement = false  抽样不放回时，表示RDD中每个元素出现的概率 [0,1]
     *
     * seed: Long = Utils.random.nextLong) 抽样种子
     */
    val newRDD1: RDD[Int] = rdd.sample(true, 3)
//    newRDD1.collect().foreach(println)

    val newRDD2: RDD[Int] = rdd.sample(false, 0.6)
    newRDD2.collect().foreach(println)
    //4.关闭连接
    sc.stop()
  }
}
