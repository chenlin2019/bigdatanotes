[{"udid":["a001"],"newsid":["abcd"],"like":["football","swimming"]},{"udid":["a002"],"newsid":["abcd2"],"like":["football","swimming"]}]


--原始数据表
CREATE EXTERNAL TABLE source(
`lines` string
)
PARTITIONED BY (dt string,ht string)
row format delimited fields terminated by '\t'
stored as textfile
location '/zhongruan/source';


load data local inpath '/opt/code/source.txt' into table source partition(dt='20200604',ht='00');


--原始数据解析表
CREATE EXTERNAL TABLE source_features(
`udid` string ,
`newsid` string, 
`liked` string
)
PARTITIONED BY (dt string,ht string)
stored as parquet
location '/zhongruan/source_features';




insert into source_features partition(dt='20200604',ht='00')values("a003","abcd3","play");


insert overwrite table source_features
PARTITION (dt='20200604',ht='00')
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


--活跃用户表
CREATE EXTERNAL TABLE source_active(
`udid` string
)
PARTITIONED BY (dt string,ht string)
row format delimited fields terminated by '\t'
stored as textfile
location '/zhongruan/source_active';

insert into source_active partition(dt='20200604',ht='00')values("a001");
insert into source_active partition(dt='20200604',ht='00')values("a003");


----点击表
CREATE EXTERNAL TABLE source_click(
`udid` string ,
`newsid` string, 
`evt_id` string
)
PARTITIONED BY (dt string,ht string)
stored as parquet
location '/zhongruan/source_click';

insert into source_click partition(dt='20200604',ht='00')values("a001","abcd","70");
insert into source_click partition(dt='20200604',ht='00')values("a002","abcd2","71");

drop table source_click


----曝光表
CREATE EXTERNAL TABLE source_exposure(
`udid` string ,
`newsid` string, 
`evt_id` string
)
PARTITIONED BY (dt string,ht string)
stored as parquet
location '/zhongruan/source_exposure';

insert into source_exposure partition(dt='20200604',ht='00')values("a001","abcd","70");
insert into source_exposure partition(dt='20200604',ht='00')values("a003","abcd3","71");


drop table source_exposure;


----宽表
CREATE EXTERNAL TABLE source_features_active_evt(
`udid`      string,
`newsid`    string,
`liked`     string, 
`evt_id`    string
)
PARTITIONED BY (dt string,ht string)
stored as parquet
location '/zhongruan/source_features_active_evt';



--  特征表关联活跃表 ，活跃为1   不活跃为0
select 
f.udid  udid,
f.newsid newsid, 
f.liked  liked,
case when a.udid is NULL then 0 else 1 end active
from 
(
select
udid,
newsid,
liked
from source_features 
where dt='20200604' and ht='00'
) f
left join 
source_active a
on f.udid=a.udid;


----
sc.udid sc.newsid   sc.evt_id   sc.dt   sc.ht   se.udid se.newsid   se.evt_id   se.dt   se.ht
a001    abcd    70  20200604    00  a001    abcd    70  20200604    00
a002    abcd2   71  20200604    00  NULL    NULL    NULL    NULL    NULL
NULL    NULL    NULL    NULL    NULL    a003    abcd3   71  20200604    00



udid    newsid  evt_id
a001    abcd    70
a002    abcd2   71
a003    abcd3   71

----
select
    case when sc.udid is not null then sc.udid when sc.udid is Null and se.udid is not null then se.udid end  udid  ,
    case when sc.newsid is not null then sc.newsid when sc.newsid is Null and se.newsid is not null then se.newsid end  newsid  , 
    case when sc.evt_id is not null then sc.evt_id when sc.evt_id is Null and se.evt_id is not null then se.evt_id end  evt_id  
from 
(   
select
    udid,
    newsid, 
    evt_id
from source_click
where dt='20200604' and ht='00'
) sc
full  join
(
select
    udid ,
    newsid, 
    evt_id
from source_exposure
where dt='20200604' and ht='00'
) se
on 
sc.udid=se.udid and sc.newsid = se.newsid;



-- 宽表插入数据
with source_features_active_tmp as(
    select 
        f.udid  udid,
        f.newsid newsid, 
        f.liked  liked,
        case when a.udid is NULL then 0 else 1 end active
    from 
    (
    select
        udid,
        newsid,
        liked
    from 
        source_features 
    where 
        dt='20200604' and ht='00'
    ) f
    left join 
        source_active a
    on f.udid=a.udid
),
click_exposure_tmp as(
    select
        case when sc.udid is not null then sc.udid when sc.udid is Null and se.udid is not null then se.udid end  udid,
        case when sc.newsid is not null then sc.newsid when sc.newsid is Null and se.newsid is not null then se.newsid end  newsid, 
        case when sc.evt_id is not null then sc.evt_id when sc.evt_id is Null and se.evt_id is not null then se.evt_id end  evt_id
    from 
    (   
        select
            udid,
            newsid, 
            evt_id
        from source_click
        where dt='20200604' and ht='00'
    ) sc
    full  join
    (
        select
            udid ,
            newsid, 
            evt_id
        from source_exposure
        where dt='20200604' and ht='00'
    ) se
    on 
    sc.udid=se.udid and sc.newsid = se.newsid
)
insert overwrite table source_features_active_evt partition(dt='20200604' , ht='00')
select
    fat.udid udid,
    fat.newsid newsid,
    fat.active active,
    cet.evt_id evt_id
from 
    source_features_active_tmp as fat
left join 
    click_exposure_tmp as cet
on 
    fat.udid=cet.udid and fat.newsid = cet.newsid;

