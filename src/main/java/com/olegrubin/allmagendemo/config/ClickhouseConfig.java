package com.olegrubin.allmagendemo.config;

import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClickhouseConfig {

    /**
     * Hardcode credentials as this is a demo application
     */
    @Bean
    public ClickHouseNode clickHouseNode() {
        return ClickHouseNode.builder()
            .host("127.0.0.1")
            .port(ClickHouseProtocol.HTTP, 18123)
            .database("allmagen")
            .credentials(ClickHouseCredentials.fromUserAndPassword(
                "username", "password"
            ))
            .build();
    }
}
