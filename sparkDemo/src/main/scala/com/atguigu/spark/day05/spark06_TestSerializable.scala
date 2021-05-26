package com.atguigu.spark.day05

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * -为什么要序列化
 *  因为在spark程序中，算子相关的操作在excutor上执行，算子之外的代码在drive端执行。
 *  在执行有些算子时，有些使用到driver里面定义的数据，这就涉及到了跨进程或者跨节点通讯
 *  所以要求传递给excutor中的数组所属的类型必须实现Serializable接口
 * -如何判断是否实现了序列化接口
 *  在提交job作业之前，其中有一行代码val cleanF = sc.clean（f），用于进行闭包检测
 *  之所以叫做闭包检测，是因为在当前函数的内部访问了外部函数的变量，属于闭包的形式。
 *
 *
 */
object spark06_TestSerializable {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)


    //4.关闭连接
    sc.stop()
  }
}
