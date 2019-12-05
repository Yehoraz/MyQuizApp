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

import com.MyQuiz.MyQuizApp.beans.PlayerMongo;
import com.MyQuiz.MyQuizApp.beans.QuizInfo;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
class QuizInfoServiceTest {

	@Autowired
	private QuizInfoService quizInfoService;

	private PlayerMongo playerMongo1 = new PlayerMongo(9991, "test1", "test1", (byte) 33);
	private PlayerMongo playerMongo2 = new PlayerMongo(9992, "test2", "test2", (byte) 33);
	private PlayerMongo playerMongo3 = new PlayerMongo(9993, "test3", "test3", (byte) 33);
	private PlayerMongo playerMongo4 = new PlayerMongo(9994, "test4", "test4", (byte) 33);
	private PlayerMongo playerMongo5 = new PlayerMongo(9995, "test5", "test5", (byte) 33);
	private PlayerMongo playerMongo6 = new PlayerMongo(9996, "test6", "test6", (byte) 33);
	private List<PlayerMongo> playersMongo = Arrays.asList(playerMongo1, playerMongo2, playerMongo3, playerMongo4,
			playerMongo5, playerMongo6);

	private QuizInfo quizInfo = new QuizInfo(0, 9991, "test1", 123, 10, playersMongo);

	private List<QuizInfo> quizInfos = new ArrayList<QuizInfo>();

	@Test
	@Order(1)
	public void addQuizInfo_Test() {
		QuizInfo quizInfoTest = quizInfoService.addQuizInfo(quizInfo);
		quizInfo.setId(quizInfoTest.getId());
		assertEquals(quizInfo, quizInfoTest);
	}

	@Test
	@Order(1)
	public void addQuizInfo2_Test() {
		quizInfo.setQuizName("test2");
		quizInfo.setWinnerPlayerId(9997);
		QuizInfo quizInfoTest = quizInfoService.addQuizInfo(quizInfo);
		quizInfo.setId(quizInfoTest.getId());
		assertEquals(quizInfo, quizInfoTest);
	}

	@Test
	@Order(1)
	public void addQuizInfo3_Test() {
		quizInfo.setQuizName("test3");
		quizInfo.setWinnerPlayerId(9998);
		QuizInfo quizInfoTest = quizInfoService.addQuizInfo(quizInfo);
		quizInfo.setId(quizInfoTest.getId());
		assertEquals(quizInfo, quizInfoTest);
	}

	@Test
	@Order(2)
	public void addQuizInfo_ExistsTest() {
		assertThrows(EntityExistsException.class, () -> quizInfoService.addQuizInfo(quizInfo));
	}

	@Test
	@Order(3)
	public void getPlayer_Test() {
		quizInfo.setQuizName("test3");
		quizInfo.setWinnerPlayerId(9998);
		assertTrue(quizInfoService.getAllQuizInfos().stream()
				.filter(qi -> (qi.getWinnerPlayerId() == quizInfo.getWinnerPlayerId()
						&& qi.getQuizName().equalsIgnoreCase(quizInfo.getQuizName())))
				.count() > 0);
	}

	@Test
	@Order(4)
	public void getAllQuizInfos_Test() {
		quizInfos.add(quizInfo);

		QuizInfo quizInfo2 = new QuizInfo();
		quizInfo2.setId(9992);
		quizInfo2.setQuizName("test2");
		quizInfo2.setQuizId(quizInfo.getQuizId());
		quizInfo2.setQuizPlayers(quizInfo.getQuizPlayers());
		quizInfo2.setWinnerPlayerId(9997);
		quizInfo2.setWinnerPlayerScore(quizInfo.getWinnerPlayerScore());

		quizInfos.add(quizInfo2);

		QuizInfo quizInfo3 = new QuizInfo();
		quizInfo3.setId(9993);
		quizInfo3.setQuizName("test3");
		quizInfo3.setQuizId(quizInfo.getQuizId());
		quizInfo3.setQuizPlayers(quizInfo.getQuizPlayers());
		quizInfo3.setWinnerPlayerId(9998);
		quizInfo3.setWinnerPlayerScore(quizInfo.getWinnerPlayerScore());

		quizInfos.add(quizInfo3);

		List<QuizInfo> quizInfos2 = quizInfoService.getAllQuizInfos();
		quizInfos2.sort((qi1, qi2) -> qi1.getQuizName().compareToIgnoreCase(qi2.getQuizName()));
		quizInfos.sort((qi1, qi2) -> qi1.getQuizName().compareToIgnoreCase(qi2.getQuizName()));

		boolean flag = true;
		if (quizInfos.size() == quizInfos2.size()) {
			for (int i = 0; i < quizInfos2.size(); i++) {
				quizInfos2.get(i).getQuizPlayers()
						.sort((p1, p2) -> p1.getFirstName().compareToIgnoreCase(p2.getFirstName()));
				quizInfos.get(i).getQuizPlayers()
						.sort((p1, p2) -> p1.getFirstName().compareToIgnoreCase(p2.getFirstName()));
				if (quizInfos2.get(i).getQuizPlayers().size() != quizInfos.get(i).getQuizPlayers().size()) {
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		if (!quizInfos2.equals(quizInfos)) {
			flag = false;
		}

		assertTrue(flag);
	}

	// stoped here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	@Test
	@Order(7)
	public void removeQuizInfo_Test() {

		quizInfoService.removeQuizInfo(quizInfo.getId());

		QuizInfo quizInfo2 = new QuizInfo();
		quizInfo2.setId(9992);
		quizInfo2.setQuizName("test2");

		quizInfos.add(quizInfo2);

		QuizInfo quizInfo3 = new QuizInfo();
		quizInfo3.setId(9993);
		quizInfo3.setQuizName("test3");

		quizInfos.add(quizInfo3);

		List<QuizInfo> quizInfos2 = quizInfoService.getAllQuizInfos();
		quizInfos2.sort((qi1, qi2) -> qi1.getQuizName().compareToIgnoreCase(qi2.getQuizName()));
		quizInfos.sort((qi1, qi2) -> qi1.getQuizName().compareToIgnoreCase(qi2.getQuizName()));
		assertEquals(quizInfos, quizInfoService.getAllQuizInfos());
	}

	@Test
	@Order(8)
	public void removeQuizInfo_NotExistsTest() {
		assertThrows(EntityNotFoundException.class, () -> quizInfoService.removeQuizInfo(quizInfo.getId()));
	}

	@Test
	@Order(9)
	public void removeAllPlayers_Test() {

		QuizInfo quizInfo2 = new QuizInfo();
		quizInfo2.setId(9992);
		quizInfo2.setQuizName("test2");

		quizInfoService.removeQuizInfo(quizInfo2.getId());

		QuizInfo quizInfo3 = new QuizInfo();
		quizInfo3.setId(9993);
		quizInfo3.setQuizName("test3");

		quizInfoService.removeQuizInfo(quizInfo3.getId());

		assertEquals(null, quizInfoService.getAllQuizInfos());
	}

}
