package com.MyQuiz.MyQuizApp.services;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityExistsException;
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
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.enums.QuizType;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
class QuizCopyServiceTest {

	@Autowired
	private QuizCopyService quizCopyService;

	private Answer answer1 = new Answer(0, "test1", false);
	private Answer answer2 = new Answer(0, "test2", false);
	private Answer answer3 = new Answer(0, "test3", false);
	private List<Answer> answers1 = Arrays.asList(answer1, answer2, answer3);

	private Answer answer4 = new Answer(0, "test1", false);
	private Answer answer5 = new Answer(0, "test2", false);
	private Answer answer6 = new Answer(0, "test3", false);
	private List<Answer> answers2 = Arrays.asList(answer4, answer5, answer6);

	private Answer answer7 = new Answer(0, "test1", false);
	private Answer answer8 = new Answer(0, "test2", false);
	private Answer answer9 = new Answer(0, "test3", false);
	private List<Answer> answers3 = Arrays.asList(answer7, answer8, answer9);

	Question question1 = new Question(0, "test1", 0, false, answers1);
	Question question2 = new Question(0, "test2", 0, false, answers2);
	Question question3 = new Question(0, "test3", 0, false, answers3);

	private List<Question> questions = Arrays.asList(question1, question2, question3);

	private List<QuizCopy> quizCopies = new ArrayList<QuizCopy>();
	private QuizCopy quizCopy = new QuizCopy(9991, "test1", QuizType.american, questions);

	@Test
	@Order(1)
	public void addQuizCopy_Test() {
		quizCopyService.addQuizCopy(quizCopy);
		assertEquals(quizCopy, quizCopyService.getQuizCopy(9991));
	}

	@Test
	@Order(1)
	public void addQuizCopy2_Test() {
		quizCopy.setId(9992);
		quizCopy.setQuizName("test2");
		quizCopyService.addQuizCopy(quizCopy);
		assertEquals(quizCopy, quizCopyService.getQuizCopy(9992));
	}

	@Test
	@Order(1)
	public void addQuizCopy3_Test() {
		quizCopy.setId(9993);
		quizCopy.setQuizName("test3");
		quizCopyService.addQuizCopy(quizCopy);
		assertEquals(quizCopy, quizCopyService.getQuizCopy(9993));
	}

	@Test
	@Order(2)
	public void addQuizCopy_ExistsTest() {
		assertThrows(EntityExistsException.class, () -> quizCopyService.addQuizCopy(quizCopy));
	}

	@Test
	@Order(3)
	public void getQuizCopy_Test() {
		quizCopy.setId(9993);
		quizCopy.setQuizName("test3");
		assertEquals(quizCopy, quizCopyService.getQuizCopy(9993));
	}

	@Test
	@Order(4)
	public void getAllQuizCopies_Test() {
		quizCopies.add(quizCopy);

		QuizCopy quizCopy2 = new QuizCopy();
		quizCopy2.setId(9992);
		quizCopy2.setQuizName("test2");
		quizCopy2.setQuizType(quizCopy.getQuizType());
		quizCopy2.setQuestions(quizCopy.getQuestions());
		quizCopies.add(quizCopy2);

		QuizCopy quizCopy3 = new QuizCopy();
		quizCopy3.setId(9993);
		quizCopy3.setQuizName("test3");
		quizCopy3.setQuizType(quizCopy.getQuizType());
		quizCopy3.setQuestions(quizCopy.getQuestions());
		quizCopies.add(quizCopy3);

		List<QuizCopy> quizCopies2 = quizCopyService.getAllQuizCopies();
		quizCopies2.sort((qc1, qc2) -> qc1.getQuizName().compareToIgnoreCase(qc2.getQuizName()));
		quizCopies.sort((qc1, qc2) -> qc1.getQuizName().compareToIgnoreCase(qc2.getQuizName()));
		boolean flag = true;
		if (quizCopies2.size() == quizCopies.size()) {
			for (int i = 0; i < quizCopies2.size(); i++) {
				quizCopies2.get(i).getQuestions()
						.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
				quizCopies.get(i).getQuestions()
						.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
				if (quizCopies2.get(i).getQuestions().size() == quizCopies.get(i).getQuestions().size()) {
					for (int j = 0; j < quizCopies2.size(); j++) {
						quizCopies2.get(i).getQuestions().get(j).getAnswers()
								.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
						quizCopies.get(i).getQuestions().get(j).getAnswers()
								.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
					}
				} else {
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		for (int i = 0; i < quizCopies2.size(); i++) {
			if (!quizCopies.get(i).equals(quizCopies2.get(i))) {
				flag = false;

			}
		}
		assertTrue(flag);
	}

	@Test
	@Order(5)
	public void updateQuizCopy_Test() {
		quizCopy.setQuizName("updated");
		quizCopyService.updateQuizCopy(quizCopy);
		assertEquals("updated", quizCopyService.getQuizCopy(9991).getQuizName());
	}

	@Test
	@Order(6)
	public void updateQuizCopy_NotExistsTest() {
		quizCopy.setQuizName("test2");
		quizCopy.setId(888);
		assertThrows(EntityNotFoundException.class, () -> quizCopyService.updateQuizCopy(quizCopy));
	}

	@Test
	@Order(7)
	public void removeQuizCopy_Test() {
		quizCopyService.removeQuizCopy(quizCopy);

		QuizCopy quizCopy2 = new QuizCopy();
		quizCopy2.setId(9992);
		quizCopy2.setQuizName("test2");
		quizCopy2.setQuizType(quizCopy.getQuizType());
		quizCopy2.setQuestions(quizCopy.getQuestions());
		quizCopies.add(quizCopy2);

		QuizCopy quizCopy3 = new QuizCopy();
		quizCopy3.setId(9993);
		quizCopy3.setQuizName("test3");
		quizCopy3.setQuizType(quizCopy.getQuizType());
		quizCopy3.setQuestions(quizCopy.getQuestions());
		quizCopies.add(quizCopy3);

		List<QuizCopy> quizCopies2 = quizCopyService.getAllQuizCopies();
		quizCopies2.sort((qc1, qc2) -> qc1.getQuizName().compareToIgnoreCase(qc2.getQuizName()));
		quizCopies.sort((qc1, qc2) -> qc1.getQuizName().compareToIgnoreCase(qc2.getQuizName()));
		boolean flag = true;
		if (quizCopies2.size() == quizCopies.size()) {
			for (int i = 0; i < quizCopies2.size(); i++) {
				quizCopies2.get(i).getQuestions()
						.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
				quizCopies.get(i).getQuestions()
						.sort((q1, q2) -> q1.getQuestionText().compareToIgnoreCase(q2.getQuestionText()));
				if (quizCopies2.get(i).getQuestions().size() == quizCopies.get(i).getQuestions().size()) {
					for (int j = 0; j < quizCopies2.size(); j++) {
						quizCopies2.get(i).getQuestions().get(j).getAnswers()
								.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
						quizCopies.get(i).getQuestions().get(j).getAnswers()
								.sort((a1, a2) -> a1.getAnswerText().compareToIgnoreCase(a2.getAnswerText()));
					}
				} else {
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		for (int i = 0; i < quizCopies2.size(); i++) {
			if (!quizCopies.get(i).equals(quizCopies2.get(i))) {
				flag = false;

			}
		}
		assertTrue(flag);
	}

	@Test
	@Order(8)
	public void removeQuizCopy_NotExistsTest() {
		assertThrows(EntityNotFoundException.class, () -> quizCopyService.removeQuizCopy(quizCopy));
	}

	@Test
	@Order(9)
	public void removeAllQuizCopys_Test() {
		QuizCopy quizCopy2 = new QuizCopy();
		quizCopy2.setId(9992);
		quizCopy2.setQuizName("test2");
		quizCopyService.removeQuizCopy(quizCopy2);

		QuizCopy quizCopy3 = new QuizCopy();
		quizCopy3.setId(9993);
		quizCopy3.setQuizName("test3");
		quizCopyService.removeQuizCopy(quizCopy3);

		assertEquals(0, quizCopyService.getAllQuizCopies().size());
	}
}
