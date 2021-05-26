package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    aggregateByKey:分区内和分区间的不同类型的计算
   aggregateByKey案例：
      创建一个pairRDD，取出每个分区相同key对应值的最大值，然后相加
* */
object Spark_Oper16 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)
    val listRDD: RDD[(String, Int)] = sc.makeRDD(List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8)),2)

    listRDD.glom().collect().foreach(array=> println(array.mkString(",")))

    val aggregateByKeylist: RDD[(String, Int)] = listRDD.aggregateByKey(0)(math.max(_, _), _ + _)

    aggregateByKeylist.collect().foreach(println)
  }

}
