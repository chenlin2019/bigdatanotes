-- 将hivefunction-1.0-SNAPSHOT.jar上传到HDFS上的/user/hive/jars路径下
hadoop fs -put  ./hivefunction-1.0-SNAPSHOT.jar  /zhongruan
--创建永久函数与开发好的java class关联
方法一：
create function str_Distinct as 'com.atguigu.udf.StringFieldDistinctUDF' using jar 'hdfs://hadoop102:9000/zhongruan/hivefunction-1.0-SNAPSHOT.jar';
方法二：
add jar 'hdfs://hadoop102:9000/zhongruan/hivefunction-1.0-SNAPSHOT.jar';
create temporary function str_Distinct as 'com.atguigu.udf.StringFieldDistinctUDF';


select
udid,
newsid,
str_Distinct(concat_ws(',',collect_set(liked))) liked
from 
    source_features
group by 
    udid,newsid;
 
 
---------------------------


insert overwrite table source_features
PARTITION (dt='20200604',ht='00')
select
udid,
newsid,
str_Distinct(concat_ws(',',collect_set(liked))) liked
from 
(
    select 
    regexp_replace(get_json_object(t1.json,'$.udid'),'\\[|\\]|\\"','') udid,
    regexp_replace(get_json_object(t1.json,'$.newsid'),'\\[|\\]|\\"','') newsid,
    regexp_replace(get_json_object(t1.json,'$.like'),'\\[|\\]|\\"','') liked
    from
    (
    select 
    explode(split(regexp_replace(regexp_extract(lines,  '^\\[(.+)\\]$',1),'\\}\\,\\{','\\}\\;\\{'),'\\;')) as json
    from source
    ) t1
)t2
group by 
    udid,newsid;




select 
regexp_replace(get_json_object(t1.json,'$.udid'),'\\[|\\]|\\"','') udid,
regexp_replace(get_json_object(t1.json,'$.newsid'),'\\[|\\]|\\"','') newsid,
regexp_replace(get_json_object(t1.json,'$.like'),'\\[|\\]|\\"','') liked
from
(
select 
explode(split(regexp_replace(regexp_extract(lines,  '^\\[(.+)\\]$',1),'\\}\\,\\{','\\}\\;\\{'),'\\;')) as json
from source
) t1;