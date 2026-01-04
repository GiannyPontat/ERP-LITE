package com.gp_dev.erp_lite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpLiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpLiteApplication.class, args);
	}

}
