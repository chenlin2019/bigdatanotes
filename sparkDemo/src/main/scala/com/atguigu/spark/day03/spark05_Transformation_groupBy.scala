package com.atguigu.spark.day03

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * groupBy（fun）  按照fun函数的返回值进行分组
 */
object spark05_Transformation_groupBy {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 3 创建一个RDD
    val rdd: RDD[Int] = sc.makeRDD(1 to 9, 3)
    rdd.mapPartitionsWithIndex{
      (index,datas)=>{
        println(index+"----------->"+datas.mkString(","))
        datas
      }
    }.collect()




    val newRDD: RDD[(Int, Iterable[Int])] = rdd.groupBy(_ % 4)
    newRDD.mapPartitionsWithIndex{
      (index,datas)=>{
        println(index+"----------->"+datas.mkString(","))
        datas
      }
    }.collect()


    Thread.sleep(10000)
    //4.关闭连接
    sc.stop()
  }
}
