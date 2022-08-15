package com.bank.mstransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MsTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionApplication.class, args);
	}

}
