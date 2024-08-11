package com.unemployed.coreconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CoreconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreconnectApplication.class, args);
	}
}
