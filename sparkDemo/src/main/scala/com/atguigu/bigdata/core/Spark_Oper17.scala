package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    foldByKey案例
      参数：(zeroValue: V)(func: (V, V) => V): RDD[(K, V)]
      1.作用：aggregateByKey的简化操作，seqop和combop相同
      2.需求：创建一个pairRDD，计算相同key对应值的相加结果
* */
object Spark_Oper17 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD = sc.makeRDD(List((1, 3), (1, 2), (1, 4), (2, 3), (3, 6), (3, 8)), 3)


    val flodRDD = listRDD.foldByKey(0)(_ + _)

    flodRDD.collect().foreach(println)
  }
}
