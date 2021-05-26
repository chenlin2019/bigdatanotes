package com.atguigu.spark.day05

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark05_action_save {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)


    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)

    // 保存为文本文件
    rdd.saveAsTextFile("output_TextFile")
    //保存序列化文件
    rdd.saveAsObjectFile("output_ObjectFile")
    //功能说明：将数据集中的元素以Hadoop Sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统。
    rdd.map((_, 1)).saveAsSequenceFile("output_SequenceFile")



    sc.textFile("output_TextFile").collect().foreach(println)
    sc.objectFile("output_ObjectFile").collect().foreach(println)
    sc.sequenceFile[Int,Int]("output_SequenceFile").collect().foreach(println)
    //4.关闭连接
    sc.stop()
  }
}
