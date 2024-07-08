package com.leonardo.tests.misctests;

import com.leonardo.tests.shared.cache.infrastructure.config.EnableCacheSharedLib;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@EnableCacheSharedLib
public class MiscTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiscTestsApplication.class, args);
	}

}
