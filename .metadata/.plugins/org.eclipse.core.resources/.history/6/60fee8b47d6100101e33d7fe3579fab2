package com.example.nagoyameshi.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> factory.addConnectorCustomizers((Connector connector) -> {
            connector.setProperty("maxParameterCount", "10000");
            connector.setProperty("maxPostSize", "10485760"); // 10MB
            connector.setProperty("maxSwallowSize", "-1");    // 無制限
            connector.setProperty("maxFileCount", "20");      // ★追加：受け入れるファイル数上限
        });
    }
}
