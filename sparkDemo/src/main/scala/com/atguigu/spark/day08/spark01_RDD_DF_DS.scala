package com.atguigu.spark.day08

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object spark01_RDD_DF_DS {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    /**
     * TODO:创建rdd转换df转换ds
     */
    //创建rdd转换df转换ds
    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "banzhang", 20), (2, "jinjin", 18)))
    // rdd-->df
    val df: DataFrame = rdd.toDF("id", "name", "age")
    df.show()

    //*****RDD=>DataSe*****
    rdd.map {
      case (id, name, age) => user(id, name, age)
    }.toDS().show()

    //df-->ds
    val ds: Dataset[user] = df.as[user]
    ds.show()
    val rdd2: RDD[user] = ds.rdd
    rdd2.collect().foreach(println)

    //TODO DS->DF->RDD
    val df1: DataFrame = ds.toDF()
    val rdd1: RDD[Row] = df1.rdd

  }
}


case class user(id:Int,name:String,age:Int)