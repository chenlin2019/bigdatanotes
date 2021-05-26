package com.atguigu.spark.day06

import java.sql.DriverManager

import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}

object spark04_MySql_read {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    // 读取mysql读取数据
    /**
     * sql
     * 注册驱动
     * 获取连接
     * 创建数据库炒作对象
     * 执行sql
     * 处理结果集
     * 关闭连接
     */
    /**
     * sc: SparkContext,  spark程序执行的入口，上线文对象
     * getConnection: () => Connection, 数据库连接
     * sql: String, 执行sql语句
     * lowerBound: Long, 查询起始位置
     * upperBound: Long, 查询结束位置
     * numPartitions: Int,  分区
     * mapRow: (ResultSet) => T  对结果集处理
     */

      // 数据库连接4要素
    var driver = "com.mysql.jdbc.Driver"
    var url = "jdbc:mysql://hadoop102:3306/rdd"
    var username = "root"
    var password = "000000"
    var sql:String = "select * from user where id>=? and id<=?"

    val JDBCRDD: JdbcRDD[(Int, String, Int)] = new JdbcRDD(
      sc,
      () => {
        Class.forName(driver)
        DriverManager.getConnection(url, username, password)
      },
      sql,
      1,
      20,
      2,
      rs => (rs.getInt(1), rs.getString(2), rs.getInt(3)) // 获取第一列，第二列，第三列
    )
    JDBCRDD.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
