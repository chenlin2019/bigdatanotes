package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark06_Transformation_combineByKey {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3.1 创建第一个RDD
    val list: List[(String, Int)] = List(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98))
    val scoreRDD: RDD[(String, Int)] = sc.makeRDD(list, 2)


    //3.2 groupByKey求出每一个学生的平均成绩
    scoreRDD.groupByKey().map {
      case (key, datas) =>
        (key, datas.sum / datas.size)
    }.collect().foreach(println)


    println("---------------------------------------------")

    //3.2 reduceByKey 比groupbykey效率要好
    scoreRDD.map {
      case (name, score) => {
        (name, (score, 1))
      }
    }.reduceByKey {
      case (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    }.map {
      case (name, datas) => {
        (name, datas._1 / datas._2)
      }
    }.collect().foreach(println)


    println("---------------------------------------------")


    /**
     * combineByKey
     * createCombiner: 对rdd中当前key取出，第一个value做一个初始化
     * mergeValue: 分区内计算规则,主要在分区内进行，将当前分区的value值，合并到初始化得到的
     * mergeCombiners: 分区间计算规则
     */
    scoreRDD.combineByKey(
      (_, 1),   // 第一个元素 变化结构
      (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1), //分区内：acc 是第一个元素的值经过结构变化形成的元祖，v是第二个元素的值
      (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2) // 分区间，acc1为相同的key在不同分区的聚合
    ).map{
      case (name,datas)=>{
        (name,datas._1/datas._2)
      }
    }.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

