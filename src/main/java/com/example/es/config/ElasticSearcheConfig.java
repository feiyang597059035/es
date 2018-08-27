package com.example.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html
 */

@Configuration
public class ElasticSearcheConfig {

    static final Logger logger = LoggerFactory.getLogger(ElasticSearcheConfig.class);

    @Value("${spring.elasticsearch.host}")
    private String host;//es地址

    @Value("${spring.elasticsearch.port}")
    private Integer prot;


    @Bean(name = "transportClient")
    public TransportClient client() {
        TransportClient client = null;
        try {

            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch")
                    .put("client.transport.sniff", true)
                    .build();// 是否开启探嗅
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), prot));
        } catch (UnknownHostException e) {
            logger.error("创建es client失败！！！,exception={}", e.getMessage());
            System.exit(-1);
        }
        return client;
    }
}