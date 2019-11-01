package com.neo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class HelloController {
    private final Logger logger = LoggerFactory.getLogger(FallbackProvider.class);

    @RequestMapping("/hello")
    public String index() {
        logger.info("request  name is 微服务调用链路");
        return "this is first messge  微服务调用链路";
    }

    @RequestMapping("/hello1")
    public String index1(@RequestParam String name) {
        logger.info("request  name is "+name);
        return "hello "+name+"，测试1";
    }



    @RequestMapping("/hello2")
    public String index2(@RequestParam String name) {
        logger.info("request  name is "+name);
        return "hello "+name+"，测试2";
    }
}