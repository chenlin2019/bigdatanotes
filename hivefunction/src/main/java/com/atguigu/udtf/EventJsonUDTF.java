package com.atguigu.udtf;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static sun.misc.Version.println;

public class EventJsonUDTF extends GenericUDTF {

    /**
     *  指定输出参数额名称和类型
     * @param argOIs
     * @return
     * @throws UDFArgumentException
     */
    @Deprecated
    public StructObjectInspector initialize(ObjectInspector[] argOIs)throws UDFArgumentException {
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<ObjectInspector> fieldsType = new ArrayList<ObjectInspector>();

        // 定义返回两个字段
        fieldNames.add("event_name");
        fieldsType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("event_json");
        fieldsType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        // 定义输出的返回值的名和返回值的类型
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldsType);
    }


    @Override
    public void process(Object[] objects) throws HiveException {
        // 获取传入的et
        String input = objects[0].toString();

        // 如果传进来的数据为空，直接返回过滤掉该数据
        if (StringUtils.isBlank(input)) {
            return;
        } else {

            try {
                // 获取一共有几个事件（ad/facoriters）,ja就是事件数组，每个事件包括 key：“ett”，“en”，“kv”
                JSONArray ja = new JSONArray(input);

                if (ja == null)
                    return;

                // 循环遍历每一个事件，每个事件包括 key：“ett”，“en”，“kv”，三个map，
                for (int i = 0; i < ja.length(); i++) {
                    // 封装输出字段
                    String[] result = new String[2];

                    try {
                        // 取出每个的事件名称
                        result[0] = ja.getJSONObject(i).getString("en");

                        // 取出当前一个事件整体
                        result[1] = ja.getString(i);
                    } catch (JSONException e) {
                        continue;
                    }

                    // 将结果返回
                    forward(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws HiveException {}


    public static void main(String[] args) throws JSONException, HiveException {
        String et ="[{\"ett\":\"1541146624055\",\"en\":\"display\",\"kv\":{\"goodsid\":\"n4195\",\"copyright\":\"ESPN\",\"content_provider\":\"CNN\",\"extend2\":\"5\",\"action\":\"2\",\"extend1\":\"2\",\"place\":\"3\",\"showtype\":\"2\",\"category\":\"72\",\"newstype\":\"5\"}},{\"ett\":\"1541213331817\",\"en\":\"loading\",\"kv\":{\"extend2\":\"\",\"loading_time\":\"15\",\"action\":\"3\",\"extend1\":\"\",\"type1\":\"\",\"type\":\"3\",\"loading_way\":\"1\"}},{\"ett\":\"1541126195645\",\"en\":\"ad\",\"kv\":{\"entry\":\"3\",\"show_style\":\"0\",\"action\":\"2\",\"detail\":\"325\",\"source\":\"4\",\"behavior\":\"2\",\"content\":\"1\",\"newstype\":\"5\"}},{\"ett\":\"1541202678812\",\"en\":\"notification\",\"kv\":{\"ap_time\":\"1541184614380\",\"action\":\"3\",\"type\":\"4\",\"content\":\"\"}},{\"ett\":\"1541194686688\",\"en\":\"active_background\",\"kv\":{\"active_source\":\"3\"}}]";
        Object[] objects = new Object[1];
        objects[0]=et;
        new EventJsonUDTF().process(objects);
    }
}
