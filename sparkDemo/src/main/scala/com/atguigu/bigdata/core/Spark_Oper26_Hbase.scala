package com.atguigu.bigdata.core

import java.sql.{Connection, PreparedStatement}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{Cell, CellUtil, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object Spark_Oper26_Hbase {
  def main(args: Array[String]): Unit = {

    //  1.创建spark配置信息
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("JdbcRDD")

    //  2.创建SparkContext
    val sc = new SparkContext(sparkConf)

    // 3、构建HBase配置信息
    val conf: Configuration = HBaseConfiguration.create()
    //    把hbase-site.xml文件放到源文件夹下后可以不用写
    //    conf.set("hbase.zookeeper.quorum", "hadoop102,hadoop103,hadoop104")

/*

    conf.set(TableInputFormat.INPUT_TABLE, "student") // 设置查询的表
    // classOf 表示取这个类的类型
    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    // 查询hbase数据
    hbaseRDD.foreach {
      case (rowkey, result) => {
        val cells: Array[Cell] = result.rawCells()  // 获取列
        for (cell <- cells) {
          println(Bytes.toString(CellUtil.cloneValue(cell)))
        }
      }
    }
*/

    // 插入数据
    val dataRDD: RDD[(String, String)] = sc.makeRDD(List(("1002", "zhangsan"), ("1003", "lisi"), ("1004", "wangwu")))
    val putRDD = dataRDD.map {
      case (rowkey, name) => {
        val put = new Put(Bytes.toBytes(rowkey))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(name))
        (new ImmutableBytesWritable(Bytes.toBytes(rowkey)), put)
      }
    }

    val jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "student")
    putRDD.saveAsHadoopDataset(jobConf)

    sc.stop()
  }
}

