package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
//导入解析json所需的包
import scala.util.parsing.json.JSON

object Spark_Oper24_JSON {
  def main(args: Array[String]): Unit = {

    //1.初始化spark配置信息并建立与spark的连接
    val confing: SparkConf = new SparkConf().setAppName("Practice").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(confing)

    val json: RDD[String] = sc.textFile("in/user.json")

    //  解析json数据
    val result: RDD[Option[Any]] = json.map(JSON.parseFull)

    result.foreach(println)

    sc.stop()

  }
}

