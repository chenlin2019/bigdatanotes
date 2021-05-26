package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable



object spark07_myAccumulator {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[(String)] = sc.makeRDD(List("hello","hello","haha","scala","spark","hadoop"))

    // 创建累加器对象
    val myAcc = new myAccumulator
    // 注册累加器
    sc.register(myAcc)

    // 使用累加器
    rdd.foreach{
      word =>{
        myAcc.add(word)
      }
    }


    println(myAcc.value)

    //4.关闭连接
    sc.stop()
  }
}

/**
 * 自定义累加器
 * 统计出RDD中所有以h开头的单词
 * -AccumulatorV2[输入，输出]
 *  更具自己的业务设置累加器输入输出的类型
 *
 */
class myAccumulator extends AccumulatorV2[String,mutable.Map[String,Int]]{
  // 定义一个集合，储存单词和出现次数r

  var map = mutable.Map[String, Int]()

  //是否为初始化状态
  override def isZero: Boolean = map.isEmpty

  // 拷贝,
  override def copy(): AccumulatorV2[String, mutable.Map[String, Int]] = {
    val newAcc = new myAccumulator
    newAcc.map = this.map
    newAcc

  }

  // 重置，恢复到初始状态
  override def reset(): Unit = {
    map.clear()
  }

  // 累加器中添加元素，v是rdd中元素
  // 统计出RDD中所有以h开头的单词
  override def add(v: String): Unit = {
    if(v.startsWith("h")){
      // getOrElse 获取key为v的值，没有的话补0
      map(v) = map.getOrElse(v,0)+1
    }

  }


  // 合并
  override def merge(other: AccumulatorV2[String, mutable.Map[String, Int]]): Unit = {
    // 当前Excutor的map
    var map1 = map
    // 另一个Excutor的map
    var map2 = other.value

    // 两个map合并
    map = map1.foldLeft(map2) {
          //mm 表示map2，kv表示map1中的每一个元素
      (mm, kv) => {
        // 指定合并规则
        val k: String = kv._1
        val v: Int = kv._2
        // 根据map1中元素的key，到map2中获取value
        mm(k) = mm.getOrElse(k, 0) + v
        mm
      }
    }
  }


  //获得累加器的值
  override def value: mutable.Map[String, Int] = map
}