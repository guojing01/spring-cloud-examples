package com.neo.controller;

import com.neo.command.HelloWorldFallbackCommand;
import com.neo.remote.HelloRemote;
import com.neo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RestController
public class ConsumerController {

    @Autowired
    HelloRemote HelloRemote;
    @Autowired
    HelloService helloService;
	
    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        return HelloRemote.hello(name);
    }

    @RequestMapping("/hello2")
    public String index2() {
        try {
            return helloService.helloService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "111111";
    }

    @RequestMapping("/hello3")
    public String index3() {
        String result ="";

        try {
            HelloWorldFallbackCommand command = new HelloWorldFallbackCommand("ExampleGroup-rcb");
            result = command.execute();
            System.out.println("=====同步执行结果："+ result);
            Future<String> future = command.queue();
            result = future.get(100, TimeUnit.MILLISECONDS); //get操作不能超过command定义的超时时间,默认:1秒
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=====异步执行结果："+ result);
        return result;

    }
}