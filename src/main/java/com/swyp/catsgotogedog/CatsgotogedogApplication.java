package com.swyp.catsgotogedog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CatsgotogedogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatsgotogedogApplication.class, args);
	}

}
