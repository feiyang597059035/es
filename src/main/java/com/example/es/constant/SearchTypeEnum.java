package com.example.es.constant;



/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
public enum SearchTypeEnum {

    GOODS_COLD("goods", "cold");

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String index;
    private String type;

    SearchTypeEnum(String index, String type) {
        this.index = index;
        this.type = type;
    }


}
