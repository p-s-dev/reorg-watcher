package com.psdev.monitor.chainreorg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"com.psdev.monitor.chainreorg"})
@EnableAutoConfiguration
public class ApplicationConfig {


    @Value("#{${web3.monitor.blocks}}")
    private Map<String,String> monitorRpcUrls;

    @Bean
    public Map<String, Web3j> rpcClients() {
        Map m = new HashMap();
        for(Map.Entry<String,String> entry : monitorRpcUrls.entrySet()) {
            m.put(entry.getKey(), Web3j.build(new HttpService(entry.getValue())));
        }
        return m;
    }

}
