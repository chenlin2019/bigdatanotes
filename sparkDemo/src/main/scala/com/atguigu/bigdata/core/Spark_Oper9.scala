package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*

* */
object Spark_Oper9 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(List(1,2,3,4,5,1,2,3,7,8,9,5,10),2)

    // 去重,shufflle ,最后的数据被打乱重组
//    val distinctRDD: RDD[Int] = listRDD.distinct()
    // 去重后，两个分区保存
    val distinctRDD: RDD[Int] = listRDD.distinct(3)

    distinctRDD.collect().foreach(println)
    distinctRDD.saveAsTextFile("output")  // 打印输出文件
  }
}
