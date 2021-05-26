package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

object spark01_Transformation_partitionBy {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "aaa"), (2, "bbb"), (3, "ccc")), 3)
    val newRDD: RDD[(Int, String)] = rdd.partitionBy(new MyParitioner(3))

    newRDD.mapPartitionsWithIndex((index,datas)=>{
      println(index+"------------------>"+datas.mkString(","))
      datas
    }).collect()



    //4.关闭连接
    sc.stop()
  }


}

// 自定义分区器 extends 继承Partitioner类
class MyParitioner(partitions: Int) extends Partitioner {
  /**
   * 分区个数
   * @return
   */
  override def numPartitions: Int = partitions

  /**
   * 分区规则
   * @param key
   * @return
   */
  override def getPartition(key: Any): Int = {


    if (key.toString.startsWith("1")) {
      0
    } else if (key.toString.startsWith("2")) {
      1
    } else {
      2
    }
  }
}