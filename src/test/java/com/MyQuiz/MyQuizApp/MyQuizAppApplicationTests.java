package com.MyQuiz.MyQuizApp;

import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.enums.QuizType;
import com.MyQuiz.MyQuizApp.repos.QuizCopyRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyQuizAppApplicationTests {

	@Autowired
	private QuizCopyRepository repo;
	
	@Test
	public void test2() {
		Map<Long, Long> playerans = new HashMap<Long, Long>();
		QuizCopy qCopy = repo.findById(507665655715316096l).orElse(null);
		System.out.println("copy is: " + qCopy);
		qCopy.getQuestions().forEach(q-> {
			playerans.put(q.getId(), q.getAnswers().get(2).getId());
		});
		System.out.println("answer is: " + playerans);
		QuizPlayerAnswers quizPlayerAnswers = new QuizPlayerAnswers(1234, 0, 0, playerans);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> responseEntity = restTemplate.postForEntity("" + "/answer/" + 507665655715316096l, quizPlayerAnswers, String.class);
		System.out.println(responseEntity);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

}
