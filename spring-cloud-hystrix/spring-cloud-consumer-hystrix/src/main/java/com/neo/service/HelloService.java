package com.neo.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2019/5/23
 */
@Service
public class HelloService {
    @Autowired
    private RestTemplate restTemplate;

    //请求熔断注解，当服务出现问题时候会执行fallbackMetho属性的名为helloFallBack的方法
    @HystrixCommand(fallbackMethod = "helloFallBack",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "2000")
    })
    public String helloService() throws ExecutionException, InterruptedException {
        return restTemplate.getForEntity("http://localhost:9000/hello",String.class).getBody();
    }

    public String helloFallBack(){
      return "error";
    }
}