package com.example.es.service.impl;


import com.example.es.constant.SearchTypeEnum;
import com.example.es.service.GoodsSearchService;
import com.example.es.vo.request.GoodsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Component
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    private TransportClient transportClient;


    @Override
    public Boolean createIndexByMap(GoodsRequest goodsRequest) {
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.generateIndexMap())
                .get();
        return response.status().getStatus() == RestStatus.CREATED.getStatus();
    }

    @Override
    public Boolean createIndexByObject(GoodsRequest goodsRequest) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json=mapper.writeValueAsString(goodsRequest);
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(json)
                .get();
        /*IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.createIndexObject())
                .get();*/


        return response.status().getStatus() == RestStatus.CREATED.getStatus();

    }

    @Override
    public Boolean createIndexByJson(GoodsRequest goodsRequest) throws IOException {
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.createIndexObject().toString())
                .get();
        return response.status().getStatus() == RestStatus.CREATED.getStatus();
    }

    @Override
    public Boolean deleteDoc(List<Object> ids) {
        //删除单条
        DeleteResponse deleteResponse = this.transportClient.
                prepareDelete(SearchTypeEnum.GOODS_COLD.getIndex(),
                        SearchTypeEnum.GOODS_COLD.getType(),
                        ids.get(0).toString())
                .execute()
                .actionGet();
        System.out.println(deleteResponse.status().getStatus());
        //使用bulk 批量删除
        QueryBuilder queryBuilder = QueryBuilders.termsQuery("goodId", ids);
        SearchResponse searchResponse = transportClient.prepareSearch(SearchTypeEnum.GOODS_COLD.getIndex())
                .setTypes(SearchTypeEnum.GOODS_COLD.getType())
                .setQuery(queryBuilder)
                .setSize(100)
                .execute().actionGet();

        SearchHit[] hits = searchResponse.getHits().getHits();
        BulkResponse responses = null;
        if (hits.length > 0) {
            // 开启批量删除
            BulkRequestBuilder bulkfresh = transportClient
                    .prepareBulk()
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            for (SearchHit searchHit : hits) {
                DeleteRequest deleteRequest = new DeleteRequest(SearchTypeEnum.GOODS_COLD.getIndex()
                        , SearchTypeEnum.GOODS_COLD.getType(), searchHit.getId());
                bulkfresh.add(deleteRequest);
            }
            // 执行
            responses = bulkfresh.execute().actionGet();
        }
        return responses != null && responses.status().getStatus() == 200;
    }

    @Override
    public Map<String, Object> searchGoodById(String goodId) {

        GetResponse response = transportClient.prepareGet(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodId).get();
       return response.getSourceAsMap();

    }

    @Override
    public boolean updateDocById(GoodsRequest goodsRequest) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(SearchTypeEnum.GOODS_COLD.getIndex());
        updateRequest.type(SearchTypeEnum.GOODS_COLD.getType());
        updateRequest.id(goodsRequest.getGoodId().toString());
        XContentBuilder builder = goodsRequest.createIndexObject();//创建bulider
        updateRequest.doc(builder);
        UpdateResponse updateResponse = transportClient.update(updateRequest).get();
        return updateResponse.status().getStatus() == 200;
    }

    @Override
    public List<Map<String, Object>> matchAll(String querySortKey) {

        List<Map<String, Object>> result = new ArrayList<>();
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SortBuilder sortBuilder = SortBuilders.fieldSort(querySortKey).order(SortOrder.DESC);
        SearchResponse searchResponse = this.transportClient.prepareSearch()
                .setQuery(queryBuilder)
                .addSort(sortBuilder)
                .execute().actionGet(1000L);
        if (searchResponse.status().getStatus() == 200) {
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hitIndex : hits) {
                System.out.println("getSourceAsString=" + hitIndex.getSourceAsString());
                System.out.println("遍历高亮集合，打印高亮片段:");
                Map<String, DocumentField> fiels = hitIndex.getFields();
                System.out.println("fiels=" + fiels);
                Map<String, Object> sourceAsMap = hitIndex.getSourceAsMap();
                System.out.println("sourceAsMap=" + sourceAsMap);
                result.add(sourceAsMap);
            }
        }
        return result;
    }

    /**
     * 创建索引的mapping
     * @return
     */
    @Override
    public Boolean createIndexMapping() {
        CreateIndexRequestBuilder cib=transportClient.admin().indices().prepareCreate("gggg9");
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
                    .field("analyzer","ik_max_word") //索引用最大粒度索引
                    .field("search_analyzer","ik_smart") //检索用最小粒度搜索
                    .startObject("fields")
                    .startObject("keyvv")
                    .field("type","keyword")
                    .endObject()
                    .endObject()
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
        cib.addMapping("vv9", mapping);



        CreateIndexResponse res=cib.execute().actionGet();


        return  true;
    }

    /**
     * 转换为对象
     * @param hits
     * @param clazz
     * @param <T>
     * @return
     * @throws SearchException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <T> List<T> convert(SearchHits hits, Class<T> clazz) throws SearchException, IllegalAccessException, InstantiationException {
       /* if(!clazz.isAnnotationPresent(Data.class)){
            //throw new SearchException("The Data Annotation is not present.");
        }*/
       List<T> list = new ArrayList<>();
        /*Field[] fields = clazz.getDeclaredFields();
        for(SearchHit searchHit : hits){
            T obj = clazz.newInstance();
            Map<String, Object> map = searchHit.getSourceAsMap();
            for (Field field : fields) {
                String column = field.getName();
                if (field.isAnnotationPresent(DataField.class)) {
                    column = field.getAnnotation(DataField.class).value();
                }
                field.setAccessible(true);
                Object valueObj = map.get(column);
                try{
                    if(valueObj != null) {
                        field.set(obj, ObjectParser.parse(field.getType(), valueObj.toString()));
                    }
                }catch (Exception e){
                    LOGGER.error("Convert set field error.Column=" + column + ",value=" + valueObj, e);
                }
            }
            list.add(obj);
        }*/
        return list;
    }



}
