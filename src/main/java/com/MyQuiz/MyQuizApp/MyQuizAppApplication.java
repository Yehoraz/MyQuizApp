package com.MyQuiz.MyQuizApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MyQuizAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MyQuizAppApplication.class, args);
	}
	
}
