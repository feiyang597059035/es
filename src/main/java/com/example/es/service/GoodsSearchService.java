package com.example.es.service;


import com.example.es.vo.request.GoodsRequest;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
public interface GoodsSearchService {

    Boolean createIndexByMap(GoodsRequest goodsRequest);

    Boolean createIndexByObject(GoodsRequest goodsRequest) throws IOException;

    Boolean createIndexByJson(GoodsRequest goodsRequest) throws IOException;

    //查询单个document
    Map<String, Object> searchGoodById(String goodId);

    Boolean deleteDoc(List<Object> ids);

    //update 单个doucument
    boolean updateDocById(GoodsRequest goodsRequest) throws IOException, ExecutionException, InterruptedException;

    //match all query

    List<Map<String, Object>> matchAll(String querySortKey);

    Boolean createIndexMapping();
}
