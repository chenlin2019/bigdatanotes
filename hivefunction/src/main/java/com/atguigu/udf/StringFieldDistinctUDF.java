package com.atguigu.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.ArrayList;
import java.util.List;

/**
 * String 字段去重
 */
public class StringFieldDistinctUDF extends UDF {

    public String evaluate(String data) {

        String[] arrayList = data.split(",");

        List<String> list = new ArrayList<String>();
        for (String s : arrayList) {
            if(!list.contains(s)){
                list.add(s);
            }
        }
        String result= list.toString().substring(1,list.toString().length()-1);
        return result;
    }


    public static void main(String[] args) {
        String line = "football,swimming,football,swimming,Basketball";
        String re = new StringFieldDistinctUDF().evaluate(line);
        System.out.println(re);
    }
}
