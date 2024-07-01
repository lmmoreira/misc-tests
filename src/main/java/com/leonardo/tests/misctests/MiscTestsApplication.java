package com.leonardo.tests.misctests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
public class MiscTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiscTestsApplication.class, args);
	}

}
