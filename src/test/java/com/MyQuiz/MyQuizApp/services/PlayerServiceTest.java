package com.MyQuiz.MyQuizApp.services;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.util.ArrayList;
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
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.enums.QuizType;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.exceptions.QuizServerException;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
class PlayerServiceTest {

	@Autowired
	private PlayerService playerService;

	@Autowired
	private QuizRepository quizRepository;

	private List<Player> players = new ArrayList<Player>();

	private Player player = new Player(9991l, "test1", "test1", (byte) 25);

	@Test
	@Order(1)
	public void createQuiz_Test() throws QuizServerException, QuizException, InvalidInputException {
		List<Answer> answers = new ArrayList<Answer>();
		List<Answer> answers1 = new ArrayList<Answer>();
		List<Answer> answers2 = new ArrayList<Answer>();
		List<Question> questions = new ArrayList<Question>();

		Answer answer = new Answer(0, "a", false);
		Answer answer1 = new Answer(0, "b", false);
		Answer answer2 = new Answer(0, "c", true);
		Answer answer3 = new Answer(0, "d", false);
		answers.add(answer);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		Question question = new Question(0, "what is it?", 0, false, answers);

		Answer answer4 = new Answer(0, "a", false);
		Answer answer5 = new Answer(0, "b", false);
		Answer answer6 = new Answer(0, "c", true);
		Answer answer7 = new Answer(0, "d", false);
		answers1.add(answer4);
		answers1.add(answer5);
		answers1.add(answer6);
		answers1.add(answer7);
		Question question1 = new Question(0, "what is it1?", 0, false, answers1);

		Answer answer8 = new Answer(0, "a", false);
		Answer answer9 = new Answer(0, "b", false);
		Answer answer10 = new Answer(0, "c", true);
		Answer answer11 = new Answer(0, "d", false);
		answers2.add(answer8);
		answers2.add(answer9);
		answers2.add(answer10);
		answers2.add(answer11);
		Question question2 = new Question(0, "what is it2?", 0, false, answers2);

		questions.add(question);
		questions.add(question1);
		questions.add(question2);

		Quiz quiz = new Quiz(0l, "my quiz", 123l, QuizType.american, null, 0,
				new Date(System.currentTimeMillis()), null, null, 1000000000l, false, questions,
				new ArrayList<Player>(), new ArrayList<QuizPlayerAnswers>());
		Quiz quiz2 = playerService.createQuiz(quiz);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(quiz2);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertEquals(quiz, quiz2);
	}

//	@Test
//	@Order(1)
//	public void addPlayer_Test() {
//		playerService.addPlayer(player);
//		assertEquals(player, playerService.getPlayerById(9991));
//	}
//
//	@Test
//	@Order(1)
//	public void addPlayer2_Test() {
//		player.setId(9992);
//		player.setFirstName("test2");
//		player.setLastName("test2");
//		player.setAge((byte) 50);
//		playerService.addPlayer(player);
//		assertEquals(player, playerService.getPlayerById(9992));
//	}
//
//	@Test
//	@Order(1)
//	public void addPlayer3_Test() {
//		player.setId(9993);
//		player.setFirstName("test3");
//		player.setLastName("test3");
//		player.setAge((byte) 100);
//		playerService.addPlayer(player);
//		assertEquals(player, playerService.getPlayerById(9993));
//	}
//
//	@Test
//	@Order(2)
//	public void addPlayer_ExistsTest() {
//		assertThrows(EntityExistsException.class, () -> playerService.addPlayer(player));
//	}
//
//	@Test
//	@Order(3)
//	public void getPlayer_Test() {
//		player.setId(9993);
//		player.setFirstName("test3");
//		player.setLastName("test3");
//		player.setAge((byte) 100);
//		assertEquals(player, playerService.getPlayerById(9993));
//	}
//
//	@Test
//	@Order(4)
//	public void getAllPlayers_Test() {
//		players.add(player);
//
//		Player player2 = new Player();
//		player2.setId(9992);
//		player2.setFirstName("test2");
//		player2.setLastName("test2");
//		player2.setAge((byte) 50);
//		players.add(player2);
//
//		Player player3 = new Player();
//		player3.setId(9993);
//		player3.setFirstName("test3");
//		player3.setLastName("test3");
//		player3.setAge((byte) 100);
//		players.add(player3);
//
//		List<Player> players2 = playerService.getAllPlayers();
//		players2.sort((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
//		players.sort((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
//		assertEquals(players, players2);
//	}
//
//	@Test
//	@Order(5)
//	public void updatePlayer_Test() {
//		player.setFirstName("updated");
//		playerService.updatePlayer(player);
//		assertEquals("updated", playerService.getPlayerById(9991).getFirstName());
//	}
//
//	@Test
//	@Order(6)
//	public void updatePlayer_NotExistsTest() {
//		player.setFirstName("test2");
//		player.setId(888);
//		assertThrows(EntityNotFoundException.class, () -> playerService.updatePlayer(player));
//	}
//
//	@Test
//	@Order(7)
//	public void removePlayer_Test() {
//
//		playerService.removePlayer(player);
//
//		Player player2 = new Player();
//		player2.setId(9992);
//		player2.setFirstName("test2");
//		player2.setLastName("test2");
//		player2.setAge((byte) 50);
//		players.add(player2);
//
//		Player player3 = new Player();
//		player3.setId(9993);
//		player3.setFirstName("test3");
//		player3.setLastName("test3");
//		player3.setAge((byte) 100);
//		players.add(player3);
//
//		List<Player> players2 = playerService.getAllPlayers();
//		players2.sort((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
//		players.sort((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
//		assertEquals(players, playerService.getAllPlayers());
//	}
//
//	@Test
//	@Order(8)
//	public void removePlayer_NotExistsTest() {
//		assertThrows(EntityNotFoundException.class, () -> playerService.removePlayer(player));
//	}
//
//	@Test
//	@Order(9)
//	public void removeAllPlayers_Test() {
//
//		Player player2 = new Player();
//		player2.setId(9992);
//		player2.setFirstName("test2");
//		player2.setLastName("test2");
//		player2.setAge((byte) 50);
//		playerService.removePlayer(player2);
//
//		Player player3 = new Player();
//		player3.setId(9993);
//		player3.setFirstName("test3");
//		player3.setLastName("test3");
//		player3.setAge((byte) 100);
//		playerService.removePlayer(player3);
//
//		assertEquals(null, playerService.getAllPlayers());
//	}

}
