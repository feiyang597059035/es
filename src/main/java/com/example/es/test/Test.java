package com.example.es.test;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * luog
 * 2018/8/27 10:26
 */
public class Test {

    public  static  void main(String[] args){
        XContentBuilder mapping = null;
        try {


            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties") //设置之定义字段
                    .startObject("author")
                    .field("type","keyword") //设置数据类型
                    .endObject()
                    .startObject("title")
                    .field("type","keyword")
                    .endObject()
                    .startObject("content")
                    .field("type","keyword")
                    .endObject()
                    .startObject("price")
                    .field("type","keyword")
                    .endObject()
                    .startObject("view")
                    .field("type","keyword")
                    .endObject()
                    .startObject("tag")
                    .field("type","text")

                    .endObject()
                    .startObject("date")
                    .field("type","date")  //设置Date类型
                    .field("format","yyyy-MM-dd HH:mm:ss") //设置Date的格式
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
