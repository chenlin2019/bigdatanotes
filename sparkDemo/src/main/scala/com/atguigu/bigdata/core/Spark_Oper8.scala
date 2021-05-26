package com.atguigu.bigdata.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
  sample(withReplacement, fraction, seed) 案例
  withReplacement表示是抽出的数据是否放回，true为有放回的抽样，false为无放回的抽样，seed用于指定随机数生成器种子。

  1. 作用：以指定的随机种子随机抽样出数量为fraction的数据，

  sample算子是用来抽样用的，其有3个参数

  withReplacement：表示抽出样本后是否在放回去，true表示会放回去，这也就意味着抽出的样本可能有重复

  fraction ：抽出多少，这是一个double类型的参数,0-1之间，eg:0.3表示抽出30%

  seed：表示一个种子，根据这个seed随机抽取，一般情况下只用前两个参数就可以，那么这个参数是干嘛的呢，这个参数一般用于调试，有时候不知道是程序出问题还是数据出了问题，就可以将这个参数设置为定值

* */
object Spark_Oper8 {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordCount")


    // 创建上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[Int] = sc.makeRDD(1 to 10)

    // 给1-10打分超过0.4就可以打印
    // 元素不可以多次抽样：withReplacement=false，每个元素被抽取到的概率为0.5：fraction=0.5
    val sampleRDD: RDD[Int] = listRDD.sample(false, 0.4, 1)

    sampleRDD.collect().foreach(println)

  }
}
