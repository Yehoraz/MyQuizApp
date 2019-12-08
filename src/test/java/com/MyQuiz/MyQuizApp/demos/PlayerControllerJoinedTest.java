package com.MyQuiz.MyQuizApp.demos;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;

class PlayerControllerJoinedTest {

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void answerQuizTest() {
		Map<Long, Long> playerans = new HashMap<Long, Long>();
		QuizCopy qCopy = null;
		System.out.println("copy is: " + qCopy);
		qCopy.getQuestions().forEach(q -> {
			playerans.put(q.getId(), q.getAnswers().get(2).getId());
		});
		System.out.println("answer is: " + playerans);
		QuizPlayerAnswers quizPlayerAnswers = new QuizPlayerAnswers(1234, 0, 0, playerans);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(
				"" + "/answer/" + 507665655715316096l, quizPlayerAnswers, String.class);
		System.out.println(responseEntity);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

}
