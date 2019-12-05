package com.MyQuiz.MyQuizApp.services;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.MyQuiz.MyQuizApp.beans.Question;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
class QuestionServiceTest {

	@Autowired
	private QuestionService questionService;

	private Answer answer1 = new Answer(0, "test1", false);
	private Answer answer2 = new Answer(0, "test1", false);
	private Answer answer3 = new Answer(0, "test1", false);
	private List<Answer> answers = Arrays.asList(answer1, answer2, answer3);

	private List<Question> questions = new ArrayList<Question>();
	private Question question = new Question(9991l, "test1", 0, false, answers);
	private Question questionTest = null;

	@Test
	@Order(1)
	public void addQuestion_Test() {
		questionTest = questionService.addQuestion(question);
		assertEquals(question.getQuestionText(), questionTest.getQuestionText());
	}

	@Test
	@Order(1)
	public void addQuestion2_Test() {
		question.setQuestionText("test2");
		question.setApproved(false);
		questionTest = questionService.addQuestion(question);
		assertEquals(question.getQuestionText(), questionTest.getQuestionText());
	}

	@Test
	@Order(1)
	public void addQuestion3_Test() {
		question.setId(3);
		question.setQuestionText("test3");
		question.setApproved(true);
		questionTest = questionService.addQuestion(question);
		assertEquals(question.getQuestionText(), questionTest.getQuestionText());
	}

	@Test
	@Order(3)
	public void getQuestion_Test() {
		question.setQuestionText("test3");
		question.setApproved(true);
		assertTrue(questionService.getAllQuestions().stream().filter(
				a -> (a.isApproved() == true && a.getQuestionText().equalsIgnoreCase(question.getQuestionText())))
				.count() > 0);
	}

	@Test
	@Order(4)
	public void getAllQuestions_Test() {
		questions.add(question);

		Question question2 = new Question();
		question2.setId(2);
		question2.setQuestionText("test2");
		question2.setApproved(false);
		questions.add(question2);

		Question answer3 = new Question();
		answer3.setId(3);
		answer3.setQuestionText("test3");
		answer3.setApproved(true);
		questions.add(answer3);

		List<Question> questions2 = questionService.getAllQuestions();
		questions2.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
		questions.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));

		boolean flag = true;
		if (questions.size() == questions2.size()) {
			for (int i = 0; i < questions.size(); i++) {
				if ((!questions.get(i).getQuestionText().equalsIgnoreCase(questions2.get(i).getQuestionText())
						|| questions.get(i).isApproved() != questions2.get(i).isApproved())) {
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
		questions = questionService.getAllQuestions();
		question = questions.get(1);
		long id = question.getId();
		question.setQuestionText("hello hello hello");
		questionService.updateQuestion(question);
		assertEquals(question.getQuestionText(), questionService.getQuestionById(id).getQuestionText());
	}

	@Test
	@Order(6)
	public void updateAnswer_NotExistsTest() {
		question.setQuestionText("test5");
		question.setId(888);
		assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestion(question));
	}

	@Test
	@Order(7)
	public void removeQuestion_Test() {

		questions = questionService.getAllQuestions();

		question = questions.get(0);
		questions.remove(question);
		questionService.removeQuestion(question);

		List<Question> questions2 = questionService.getAllQuestions();
		questions2.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
		questions.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
		assertEquals(questions, questionService.getAllQuestions());
	}

	@Test
	@Order(8)
	public void removeQuestion_NotExistsTest() {
		assertThrows(EntityNotFoundException.class, () -> questionService.removeQuestion(question));
	}

	@Test
	@Order(9)
	public void removeAllQuestions_Test() {
		questions = questionService.getAllQuestions();
		questions.forEach(a -> questionService.removeQuestion(a));
		assertEquals(0, questionService.getAllQuestions().size());
	}

}
