package com.atguigu.spark.day06

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}

object spark05_MySql_write {
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

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("zhaoliu", 24), ("tianqi", 26)))

    // 在循环汇总创建连接对象，每次遍历出rdd中的一个元素，都要创建一个连接对象，效率低，不推荐使用
    rdd.foreach{
      case (name,age)=>{
        //注册驱动
          Class.forName(driver)
        //获取连接
        val conn: Connection = DriverManager.getConnection(url, username, password)
        //创建数据库操作sql语句
        val sql:String = "insert into user(username,age) value(?,?)"
        // 创建数据库操作对象  prepareStatement
        val ps: PreparedStatement = conn.prepareStatement(sql)
        // 给参数赋值
        ps.setString(1,name)
        ps.setInt(2,age)
        //执行sql
        ps.executeUpdate()
        //关闭连接
        ps.close()
        conn.close()
      }
    }

    //4.关闭连接
    sc.stop()
  }
}
