package com.atguigu.bigdata.core

import java.sql.{Connection, PreparedStatement}

import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}


object Spark_Oper25_Mysql {
  def main(args: Array[String]): Unit = {

    //1.创建spark配置信息
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("JdbcRDD")

    //2.创建SparkContext
    val sc = new SparkContext(sparkConf)

    //3.定义连接mysql的参数
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://hadoop101:3306/rdd"
    val userName = "root"
    val passWd = "root"

    // 4、创建JdbcRDD
    /*
        // 查询数据
        val sql = "select name,age from user where id >=? and id <= ?"
        val jdbcRDD: JdbcRDD[Unit] = new JdbcRDD(
          sc,
          () => {
            // 获取数据库的链接对象
            Class.forName(driver)
            java.sql.DriverManager.getConnection(url, userName, passWd)
          },
          sql,
          1,
          2,
          3,
          rs => println(rs.getString(1) + "," + rs.getInt(2))
        )

        jdbcRDD.collect()
    */
    /*======================================================================================================================*/
    // 保存数据
    val dataRDD: RDD[(String, Int)] = sc.makeRDD(List(("zhangsan", 10), ("lisi", 20), ("wangwu", 30)), 2)




    /*
    // 获取数据库的链接对象
        Class.forName(driver)
        val connection: Connection = java.sql.DriverManager.getConnection(url, userName, passWd)
        当链接对象放在外面时，会遇到链接对象无法序列化的问题
        dataRDD.foreach {
          case (name, age) => {
            val sql = "insert into user(name,age) VALUES (?,?)"
            val statement: PreparedStatement = connection.prepareStatement(sql)
            statement.setString(1,name)
            statement.setInt(2,age)
            statement.executeUpdate()
            statement.close()
          }
        }
    */


    // foreach分区
    dataRDD.foreachPartition(datas => {
      // 获取数据库的链接对象
      Class.forName(driver)
      val connection: Connection = java.sql.DriverManager.getConnection(url, userName, passWd)
      datas.foreach {
        case (name, age) => {
          val sql = "insert into user(name,age) VALUES (?,?)"
          val statement: PreparedStatement = connection.prepareStatement(sql)
          statement.setString(1, name)
          statement.setInt(2, age)
          statement.executeUpdate()
          statement.close()
        }
      }
      connection.close()
    })

    sc.stop()
  }
}

