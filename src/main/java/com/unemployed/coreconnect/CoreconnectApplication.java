package com.unemployed.coreconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import com.unemployed.coreconnect.service.DeviceService;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CoreconnectApplication {
	@Autowired
	DeviceService deviceService;

	public static void main(String[] args) {
		SpringApplication.run(CoreconnectApplication.class, args);
	}
}
