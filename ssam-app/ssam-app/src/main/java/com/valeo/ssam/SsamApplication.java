package com.valeo.ssam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SsamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsamApplication.class, args);
	}

}
