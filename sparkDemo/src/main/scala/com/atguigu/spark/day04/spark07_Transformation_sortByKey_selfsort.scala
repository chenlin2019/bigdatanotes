package com.atguigu.spark.day04

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object spark07_Transformation_sortByKey_selfsort {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)
    //3.1 创建第一个RDD
    // 如果key为自定义类型，要求此自定义类型继承order
    val rdd: RDD[(student, Int)] = sc.makeRDD(List(
      (new student("zhangsan", 25), 1),
      (new student("lisi", 25), 1),
      (new student("wangwu", 25), 1),
      (new student("zhaoliu", 25), 1)
    ))

    rdd.sortByKey().collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}


// 如果key为自定义类型，要求此自定义类型继承order[泛型]：泛型为需要比较的对象
class student(var name: String, var age: Int) extends Ordered[student] with Serializable {

  // 指定比较规则 this在前表示升序
  override def compare(that: student): Int = {
    //先按名字升序，如果相同再按年龄降序
    var res: Int = this.name.compareTo(that.name)
    if (res == 0) {
      //res = that.age-this.age 作用同下
      res = that.age.compareTo(this.age)
    }
    res
  }

  override def toString = s"student($name, $age)"
}