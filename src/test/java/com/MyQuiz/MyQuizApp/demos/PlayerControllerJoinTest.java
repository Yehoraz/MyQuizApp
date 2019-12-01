package com.MyQuiz.MyQuizApp.demos;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.services.QuizService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerControllerJoinTest {

	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private QuizService quizService;
	
	private List<Quiz> quizs;

	@Test
	@Order(1)
	public void joinQuizTest() {
		quizs = quizService.getAllQuizs();
		try {
			restTemplate.put("" + "/join/" + quizs.get(0).getId() + "/" + 1234, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("joined");
		System.out.println("player is: " + quizService.getQuizById(quizs.get(0).getId()).getPlayers());
	}
	
	

	@Test
	@Order(10)
	public void leaveQuizTest() {
		quizs = quizService.getAllQuizs();
		try {
			restTemplate.put("" + "/leave/" + quizs.get(0).getId() + "/" + 1234, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("left");
		System.out.println("player is: " + quizService.getQuizById(quizs.get(0).getId()).getPlayers());
	}

}
