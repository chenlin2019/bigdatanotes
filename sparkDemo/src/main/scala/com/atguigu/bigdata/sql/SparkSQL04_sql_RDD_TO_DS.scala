package com.atguigu.bigdata.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}


object SparkSQL04_sql_RDD_TO_DS{
  def main(args: Array[String]): Unit = {
    //SparkSQL

    //SparkConf  创建配置对象
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL_Demo01")

    //SparkSession 创建sparksql的环境对象
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    // 进行转换之前，需要引入隐式转换规则
    // 这里的spark不是包的含义，是sparksession对象的名字
    import spark.implicits._

    // 创建RDD
    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 20), (2, "lisi", 30), (3, "wangwu", 40)))
    // 转换为DS
    val userRDD: RDD[User] = rdd.map {
      case (id, name, age) => {
        User(id, name, age)
      }
    }

    // rdd->ds
    val userDS: Dataset[User] = userRDD.toDS()
    userDS.show()

    // ds->rdd
    val rdd1: RDD[User] = userDS.rdd
    rdd1.foreach(user=>{
      println("id="+user.id+",name="+user.name+",age="+user.age)
    })


    // 释放资源
    spark.stop()
  }
}

