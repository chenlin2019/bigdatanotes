package com.atguigu.spark.day09

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object sparkSQL01_mysql_write {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    val rdd: RDD[User2] = spark.sparkContext.makeRDD(List(User2("lisi", 20), User2("zs", 30)))

    val ds: Dataset[User2] = rdd.toDS()
    val df: DataFrame = rdd.toDF()

    //方式1：通用的方式  format指定写出类型
    df.write
      .format("jdbc")
      .option("url", "jdbc:mysql://hadoop102:3306/test")
      .option("user", "root")
      .option("password", "000000")
      .option("dbtable", "user")
      .mode(SaveMode.Append)
      .save()


    //方式2：通过jdbc方法
    val props: Properties = new Properties()
    props.setProperty("user", "root")
    props.setProperty("password", "000000")
    df.write.mode(SaveMode.Append).jdbc("jdbc:mysql://hadoop102:3306/test", "user", props)






    // 释放资源
    spark.stop()
  }
}
case class User2(username: String, age: Long)