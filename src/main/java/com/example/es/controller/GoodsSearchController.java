package com.example.es.controller;


import com.example.es.service.GoodsSearchService;
import com.example.es.vo.request.GoodsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Slf4j
@RestController
@RequestMapping("/es/goods")
public class GoodsSearchController {


    @Autowired
    GoodsSearchService goodsSearchService;

    /**
     * 创建document 如果index type不错在会自动创建
     */
    @RequestMapping(value = "/create/doc", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean createDocByMap(@RequestBody GoodsRequest goodsRequest) {
        boolean flag = Boolean.FALSE;
        try {
            flag = this.goodsSearchService.createIndexByMap(goodsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除document
     */
    @RequestMapping("/delete/doc")
    @ResponseBody
    public Boolean delete(@RequestParam("goodIds") String goodIds) {
        return this.goodsSearchService.deleteDoc(Arrays.asList(goodIds.split(",")));
    }


    @RequestMapping("/search/{goodId}")
    @ResponseBody
    public Map<String, Object> searchById(@PathVariable("goodId") String goodId) {
        Map<String,Object> map=this.goodsSearchService.searchGoodById(goodId);
        Map<String,Object> map2=map;
        return  map;
    }

    @RequestMapping("/update/doc")
    @ResponseBody
    public Boolean updateDoc(@RequestBody GoodsRequest goodsRequest) throws InterruptedException, ExecutionException, IOException {
        return this.goodsSearchService.updateDocById(goodsRequest);
    }


    @RequestMapping("/match/all")
    @ResponseBody
    public List<Map<String, Object>> matchAll(@RequestParam("sortKey") String sortKey) {
        return this.goodsSearchService.matchAll(sortKey);
    }


    @RequestMapping("/create/mapping")
    @ResponseBody
    public Boolean createIndexMapping() {
        return goodsSearchService.createIndexMapping();
    }


}
