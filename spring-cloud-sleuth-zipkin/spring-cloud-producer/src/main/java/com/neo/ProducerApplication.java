package com.neo;

import com.neo.configuration.PercentageLocalSampler;
import com.neo.configuration.SamplerLocalProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({
		SamplerLocalProperties.class
})
public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	SamplerLocalProperties samplerLocalProperties() {
		return new SamplerLocalProperties();
	}
	@Bean
	Sampler percentageLocalSampler(SamplerLocalProperties samplerLocalProperties){
		//如果这个地方设置全局采样率，yml文件中的配置将不会生效
//		samplerLocalProperties.setPercentage(0.1F);
		return new PercentageLocalSampler(samplerLocalProperties);
	}
}
