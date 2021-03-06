package com.atguigu.udf;

import jodd.util.StringUtil;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseFieldUDF extends UDF {

    public String evaluate(String line, String jsonKeysString){
        StringBuilder sb = new StringBuilder();

        //1、 获取所有key
        String[] jsonKeys = jsonKeysString.split(",");

        // 2、 line 服务器时间json
        String[] logContent = line.split("\\|");

        // 3、校验,数组长度为2或logContent[1]为空
        if(logContent.length!=2 || StringUtil.isBlank(logContent[1])){
            return  "";
        }

        try {
            // 4、logContent[1]创建json对象
            JSONObject jsonObject = new JSONObject(logContent[1]);
            JSONObject cmjson = jsonObject.getJSONObject("cm");
            for (String jsonKey : jsonKeys) {
                jsonKey = jsonKey.trim();
                if (cmjson.has(jsonKey)){
                    sb.append(cmjson.getString(jsonKey)).append("\t");
                }else{
                    sb.append("\t");
                }
            }
            // 7、拼接事件字段
            sb.append(jsonObject.getString("et")).append("\t");
            // 8、服务器时间
            sb.append(logContent[0]).append("\t");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static void main(String[] args) {

        String line = "1541217850324|{\"cm\":{\"mid\":\"m7856\",\"uid\":\"u8739\",\"ln\":\"-74.8\",\"sv\":\"V2.2.2\",\"os\":\"8.1.3\",\"g\":\"P7XC9126@gmail.com\",\"nw\":\"3G\",\"l\":\"es\",\"vc\":\"6\",\"hw\":\"640*960\",\"ar\":\"MX\",\"t\":\"1541204134250\",\"la\":\"-31.7\",\"md\":\"huawei-17\",\"vn\":\"1.1.2\",\"sr\":\"O\",\"ba\":\"Huawei\"},\"ap\":\"weather\",\"et\":[{\"ett\":\"1541146624055\",\"en\":\"display\",\"kv\":{\"goodsid\":\"n4195\",\"copyright\":\"ESPN\",\"content_provider\":\"CNN\",\"extend2\":\"5\",\"action\":\"2\",\"extend1\":\"2\",\"place\":\"3\",\"showtype\":\"2\",\"category\":\"72\",\"newstype\":\"5\"}},{\"ett\":\"1541213331817\",\"en\":\"loading\",\"kv\":{\"extend2\":\"\",\"loading_time\":\"15\",\"action\":\"3\",\"extend1\":\"\",\"type1\":\"\",\"type\":\"3\",\"loading_way\":\"1\"}},{\"ett\":\"1541126195645\",\"en\":\"ad\",\"kv\":{\"entry\":\"3\",\"show_style\":\"0\",\"action\":\"2\",\"detail\":\"325\",\"source\":\"4\",\"behavior\":\"2\",\"content\":\"1\",\"newstype\":\"5\"}},{\"ett\":\"1541202678812\",\"en\":\"notification\",\"kv\":{\"ap_time\":\"1541184614380\",\"action\":\"3\",\"type\":\"4\",\"content\":\"\"}},{\"ett\":\"1541194686688\",\"en\":\"active_background\",\"kv\":{\"active_source\":\"3\"}}]}";
        String x = new BaseFieldUDF().evaluate(line, "mid,uid,vc,vn,l,sr,os,ar,md,ba,sv,g,hw,nw,ln,la,t");
        System.out.println(x);
    }
}

