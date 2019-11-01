package com.neo.configuration;

import lombok.Data;
/**
* @Author rcb
* @Description自定义uri采样率,uri是否包含
**/
@Data
public class UriSampleProperties {

    /**
     * uri是否包含配置的字符串
     */
    private String uriRegex;

    private float uriPercentage = 0.1f;

}
