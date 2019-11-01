package com.neo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

/**
* @Author rcb
* @Description 设置自定义全局采样率
 * 读取配置文件需要进入pom文件 + 启动类中配置
**/
@PropertySource("classpath:application.yml")
@ConfigurationProperties("spring.sleuth.sampler")
@Data
public class SamplerLocalProperties {


    private List<UriSampleProperties> uriSample = new ArrayList<>(0);

    private float percentage = 0.1f;

}
