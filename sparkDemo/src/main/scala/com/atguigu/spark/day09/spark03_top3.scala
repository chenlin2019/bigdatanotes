package com.atguigu.spark.day09

import java.text.DecimalFormat

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, MapType, StringType, StructField, StructType}

object spark03_top3 {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")

    //创建SparkSQL执行的入口点对象，SparkSession对象
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    // 选择数据库
    spark.sql("use default")

    //注册自定义函数
    spark.udf.register("city_remark", new CityClickUDAF)

    // --1.1从用户行为表中，查询所有点击记录，并和city_info,product_info进行连接
    spark.sql(
      """
        |select
        |    c.*,
        |    p.product_name
        |from
        |    user_visit_action u
        |join city_info c
        |on u.city_id = c.city_id
        |join product_info p
        |on p.product_id=u.click_product_id
        |where u.click_product_id > -1
        |""".stripMargin).createOrReplaceTempView("t1")

    // --1.2按照地区和商品的名称进行分组，统计出每个地区每个商品的总点击数
    spark.sql(
      """
        |select
        |    t1.area,
        |    t1.product_name,
        |    count(*) product_click_count,
        |    city_remark(t1.city_name)
        |from
        |    t1
        |group by t1.area,t1.product_name
        |""".stripMargin).createOrReplaceTempView("t2")

    // --1.3针对每个地区，对商品点击数进行降序排序
    spark.sql(
      """
        |select
        |   t2.*,
        |   row_number() over(partition by t2.area order by t2.product_click_count desc) cn
        |from
        |   t2
        |""".stripMargin).createOrReplaceTempView("t3")

    // --1.4取当前地区的前3名
    spark.sql(
      """
        |select
        |    *
        |from
        |    t3
        |where t3.cn <= 3
        |""".stripMargin).show()

    // 释放资源
    spark.stop()
  }
}

// 自定义一个UDAF聚合函数，完成城市点击率统计
class CityClickUDAF extends UserDefinedAggregateFunction {

  // 输入数据类型
  override def inputSchema: StructType = {
    StructType(Array(StructField("city_name", StringType)))
  }

  // 缓存数据类型 用map来缓存城市以及该城市点击数：北京->2
  override def bufferSchema: StructType = {
    // maptype不可变
    StructType(Array(StructField("city_count", MapType(StringType, LongType)), StructField("total_count", LongType)))
  }

  // 输出的数据类型 北京21.2%，天津13.2%，其他65.6%
  override def dataType: DataType = StringType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = Map[String, Long]()
    buffer(1) = 0L
  }

  // 分区内
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    // 从输入的数据中，获取城市的名称
    val cityName: String = input.getString(0)
    //从缓存中获取存放城市点击量的Map的集合
    val map: collection.Map[String, Long] = buffer.getMap[String, Long](0)
    //城市点击量+1
    // map(cityName) = map.getOrElse(cityName, 0L) + 1L  可变集合
    buffer(0) = map + (cityName -> (map.getOrElse(cityName, 0L) + 1L))
    //总点击量+1
    buffer(1) = buffer.getLong(1) + 1L
  }

  // 分区间
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    //获取每个节点城市点击缓存map
    val map1: Map[String, Long] = buffer1.getAs[Map[String, Long]](0)
    val map2: Map[String, Long] = buffer2.getAs[Map[String, Long]](0)

    // 合并
    buffer1(0) = map1.foldLeft(map2) {
      case (map, (k, v)) =>{
        map + (k -> (map.getOrElse(k, 0L) + v))
      }
    }


    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)

  }

  // 最终的输出. "北京21.2%，天津13.2%，其他65.6%"
  override def evaluate(buffer: Row): Any = {
    // 从缓存中获取数据
    val cityCountMap = buffer.getAs[Map[String, Long]](0)
    val totalCount = buffer.getLong(1)

    // 对map集合中城市点击记录进行降序排序，取前2
    val sortList: List[(String, Long)] = cityCountMap.toList.sortBy(-_._2).take(2)
    //计算排名前2的点击率
    var citysRatio: List[CityRemark] = sortList.map {
      case (cityName, count) => {
        CityRemark(cityName, count.toDouble / totalCount)
      }
    }
    // 如果城市个数超过两个，那么其他处理
    // 如果城市的个数超过2才显示其他
    if (cityCountMap.size > 2) {
      citysRatio = citysRatio :+ CityRemark("其他", citysRatio.foldLeft(1D)(_ - _.cityRatio))
    }
    citysRatio.mkString(", ")
  }
}
// 格式化类
case class CityRemark(cityName: String, cityRatio: Double) {
  val formatter = new DecimalFormat("0.00%")
  override def toString: String = s"$cityName:${formatter.format(cityRatio)}"
}