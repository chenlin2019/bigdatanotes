package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    1. 作用：在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的RDD
    2. 需求：创建两个pairRDD，并将key相同的数据聚合到一个迭代器。
* */
object Spark_Oper22 {
  def main(args: Array[String]): Unit = {
    val confing: SparkConf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // 创建上下文对象

    val sc: SparkContext = new SparkContext(confing)

    val listRDD1: RDD[(Int, String)] = sc.makeRDD(Array((1,"a"),(2,"b"),(3,"c"),(4,"d")))
    val listRDD2: RDD[(Int, Int)] = sc.makeRDD(Array((1,4),(2,5),(3,6)))


    val cogroupList: RDD[(Int, (Iterable[String], Iterable[Int]))] = listRDD1.cogroup(listRDD2)

    cogroupList.collect().foreach(println)
  }
}
