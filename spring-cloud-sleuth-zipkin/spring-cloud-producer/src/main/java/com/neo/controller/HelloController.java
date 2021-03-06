package com.neo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {
    private final Logger logger = LoggerFactory.getLogger(FallbackProvider.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        logger.info("request  name is "+name);
        String body = restTemplate.getForEntity("http://user/user/hello", String.class).getBody();
        return "hello ==="+body+"====，this is first messge";
    }

    @RequestMapping("/user/hello")
    public String index1(@RequestParam String name) {
        logger.info("request  name is "+name);
        return "hello "+name+"，测试1";
    }



    @RequestMapping("/test/user")
    public String index2(@RequestParam String name) {
        logger.info("request  name is "+name);
        return "hello "+name+"，测试2";
    }
}