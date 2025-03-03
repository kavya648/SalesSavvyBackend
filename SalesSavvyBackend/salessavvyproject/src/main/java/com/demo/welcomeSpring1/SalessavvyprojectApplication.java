package com.demo.welcomeSpring1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SalessavvyprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalessavvyprojectApplication.class, args);
	}

}
