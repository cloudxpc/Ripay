package com.rickxpc.ripay;

import com.rickxpc.ripay.config.WxConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WxConfig.class)
public class RipayApplication {
	public static void main(String[] args) {
		SpringApplication.run(RipayApplication.class, args);
	}
}
