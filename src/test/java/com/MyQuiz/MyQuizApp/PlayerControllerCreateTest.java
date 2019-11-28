package com.MyQuiz.MyQuizApp;

import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.enums.QuizType;
import com.MyQuiz.MyQuizApp.repos.SuggestedQuestionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerControllerCreateTest {

	@Autowired
	private SuggestedQuestionRepository sQuestionRepository;

	private RestTemplate restTemplate = new RestTemplate();
	private String BASE_QUIZ_URL = "need to add!";

	@Test
	@Order(1)
	public void addPlayerTest_New() {
		Player player = new Player(123l, "moshe", "moshon", (byte) 35);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", player,
				String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

	@Test
	@Order(1)
	public void addPlayerTest_New2() {
		Player player = new Player(1234l, "moshe2", "moshon2", (byte) 45);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", player,
				String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

	@Test
	@Order(1)
	public void addPlayerTest_New3() {
		Player player = new Player(1235l, "moshe3", "moshon3", (byte) 55);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", player,
				String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

	@Test
	@Order(2)
	public void createQuizTest_New() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		List<Question> questions = new ArrayList<Question>();
		Question question = new Question(0, "what is it?", 0, false, answers);
		Question question1 = new Question(0, "what is it1?", 0, false, answers);
		Question question2 = new Question(0, "what is it2?", 0, false, answers);
		Question question3 = new Question(0, "what is it3?", 0, false, answers);
		Question question4 = new Question(0, "what is it4?", 0, false, answers);
		Question question5 = new Question(0, "what is it5?", 0, false, answers);
		Question question6 = new Question(0, "what is it6?", 0, false, answers);
		questions.add(question);
		questions.add(question1);
		questions.add(question2);
		questions.add(question3);
		questions.add(question4);
		questions.add(question5);
		questions.add(question6);

		Quiz quiz = new Quiz(0l, "my quiz", 123l, QuizType.american, null, 0, new Date(System.currentTimeMillis()),
				null, null, 1000000000l, false, questions, new ArrayList<Player>(), new ArrayList<QuizPlayerAnswers>());

		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/createQuiz", quiz,
				String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.OK.value());
	}

	@Test
	@Order(10)
	public void suggestQuestionTest_New() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "what what?", 0, false, answers);
		restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234, question, String.class);
		SuggestedQuestion sQuestion2 = sQuestionRepository.findAll().get(0);
		assertTrue(question.equals(sQuestion2.getQuestion()));
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_AlreadyExists() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "what is it?", 0, false, answers);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_QuestionText() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "", 0, false, answers);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_QuestionId() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "something new", 0, false, answers);
		question.setId(-5);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_AnswersNull() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "something new", 0, false, answers);
		question.setAnswers(null);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_CorrectAnswerId() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "something new", 0, false, answers);
		question.setCorrectAnswerId(-5);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_AnswerId() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "something new", 0, false, answers);
		question.getAnswers().get(0).setId(-5);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(11)
	public void suggestQuestionTest_InvalidInput_AnswerText() {
		List<Answer> answers = new ArrayList<Answer>();
		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);

		Question question = new Question(0, "something new", 0, false, answers);
		question.getAnswers().get(0).setAnswerText("");
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion/" + 1234,
				question, String.class);
		assertTrue(responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value());
	}

	@Test
	@Order(12)
	public void updateSuggestedQuestion() {
		SuggestedQuestion sQuestion = sQuestionRepository.findAll().get(0);
		sQuestion.getQuestion().setQuestionText("hello from adir");
		try {
			restTemplate.put(BASE_QUIZ_URL + "/updateSuggestedQuestion/" + sQuestion.getPlayerId(), sQuestion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SuggestedQuestion sQuestion2 = sQuestionRepository.findById(sQuestion.getId()).orElse(null);
		assertTrue(sQuestion2 != null && sQuestion.getPlayerId() == sQuestion2.getPlayerId()
				&& sQuestion.getQuestion().equals(sQuestion2.getQuestion()));
	}

}
