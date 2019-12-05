package com.MyQuiz.MyQuizApp.services;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.MyQuiz.MyQuizApp.beans.Answer;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
public class AnswerServiceTest {

	@Autowired
	private AnswerService answerService;

	private List<Answer> answers = new ArrayList<Answer>();

	private Answer answer = new Answer(10, "test1", false);
	private Answer answerTest = null;

	@Test
	@Order(1)
	public void addAnswer_Test() {
		answerTest = answerService.addAnswer(answer);
		assertEquals(answer.getAnswerText(), answerTest.getAnswerText());
	}

	@Test
	@Order(1)
	public void addAnswer2_Test() {
		answer.setAnswerText("test2");
		answer.setCorrectAnswer(false);
		answerTest = answerService.addAnswer(answer);
		assertEquals(answer.getAnswerText(), answerTest.getAnswerText());
	}

	@Test
	@Order(1)
	public void addAnswer3_Test() {
		answer.setId(3);
		answer.setAnswerText("test3");
		answer.setCorrectAnswer(true);
		answerTest = answerService.addAnswer(answer);
		assertEquals(answer.getAnswerText(), answerTest.getAnswerText());
	}

	@Test
	@Order(3)
	public void getAnswer_Test() {
		answer.setAnswerText("test3");
		answer.setCorrectAnswer(true);
		assertTrue(answerService.getAllAnswers().stream().filter(
				a -> (a.isCorrectAnswer() == true && a.getAnswerText().equalsIgnoreCase(answer.getAnswerText())))
				.count() > 0);
	}

	@Test
	@Order(4)
	public void getAllAnswers_Test() {
		answers.add(answer);

		Answer answer2 = new Answer();
		answer2.setId(2);
		answer2.setAnswerText("test2");
		answer2.setCorrectAnswer(false);
		answers.add(answer2);

		Answer answer3 = new Answer();
		answer3.setId(3);
		answer3.setAnswerText("test3");
		answer3.setCorrectAnswer(true);
		answers.add(answer3);

		List<Answer> answers2 = answerService.getAllAnswers();
		answers2.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
		answers.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));

		boolean flag = true;
		if (answers.size() == answers2.size()) {
			for (int i = 0; i < answers.size(); i++) {
				if ((!answers.get(i).getAnswerText().equalsIgnoreCase(answers2.get(i).getAnswerText())
						|| answers.get(i).isCorrectAnswer() != answers2.get(i).isCorrectAnswer())) {
					flag = false;
					break;
				}
			}
		} else {
			flag = false;
		}

		assertTrue(flag);
	}

	@Test
	@Order(5)
	public void updateAnswer_Test() {
		answers = answerService.getAllAnswers();
		answer = answers.get(1);
		long id = answer.getId();
		answer.setAnswerText("hello hello hello");
		answerService.updateAnswer(answer);
		assertEquals(answer.getAnswerText(), answerService.getAnswerById(id).getAnswerText());
	}

	@Test
	@Order(6)
	public void updateAnswer_NotExistsTest() {
		answer.setAnswerText("test5");
		answer.setId(888);
		assertThrows(EntityNotFoundException.class, () -> answerService.updateAnswer(answer));
	}

	@Test
	@Order(7)
	public void removeAnswer_Test() {

		answers = answerService.getAllAnswers();

		answer = answers.get(0);
		answers.remove(answer);
		answerService.removeAnswer(answer);

		List<Answer> answers2 = answerService.getAllAnswers();
		answers2.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
		answers.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
		assertEquals(answers, answerService.getAllAnswers());
	}

	@Test
	@Order(8)
	public void removeAnswer_NotExistsTest() {
		assertThrows(EntityNotFoundException.class, () -> answerService.removeAnswer(answer));
	}

	@Test
	@Order(9)
	public void removeAllAnswers_Test() {
		answers = answerService.getAllAnswers();
		answers.forEach(a -> answerService.removeAnswer(a));
		assertEquals(null, answerService.getAllAnswers());
	}

}
