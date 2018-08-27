package com.example.es.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Getter
@Setter
public class GoodsInfo {

    private int cityId;//城市id
    private Long goodId;//商品id
    private Long shopId;//店铺id
    private String goodPicUrl;//产品图片
    private String goodName;//产品名称
    private String goodDesc;//产品描述
    private String specification;//规格
    private int price;//单价
    private int salesVolume;//销量


}
