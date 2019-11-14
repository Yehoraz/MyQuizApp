package com.MyQuiz.MyQuizApp.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizId;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;
import com.MyQuiz.MyQuizApp.services.QuizIdService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.services.SuggestedQuestionService;
import com.MyQuiz.MyQuizApp.threads.QuizCopyThread;

@RestController
public class PlayerController {

	// need to make validation methods!!!!!!!

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizCopyService quizCopyService;

	@Autowired
	private QuizIdService quizIdService;

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private SuggestedQuestionService suggestedQuestionService;
	
	@Autowired
	private QuestionService questionService;
	

	private Quiz quiz = null;
	private Player player = null;
	Thread quizCopyThread = null;

	@PostMapping("/createQuiz/{player_Id}")
	public ResponseEntity<?> createQuiz(@PathVariable long player_id, @RequestBody Quiz quiz) {
		// add role quiz manager to this player need to fix it!!!!
			quiz.setId(createQuizId());
			if (quiz.getId() > 0) {
				try {
					quizService.addQuiz(quiz);
				} catch (EntityExistsException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already exists");
				} catch (InvalidInputException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
				}
				quizCopyThread = new Thread(new QuizCopyThread(quiz, quizCopyService));
				quizCopyThread.start();
				return ResponseEntity.status(HttpStatus.OK).body("Quiz Added");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Server had a problem please try again");
			}
	}

	// need to make sure the security service adds the principal id as the
	// playeranswer.getplayerid()!!!!!!!!!!!!!!!!!!!!!!!!!!
	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quiz_id, @RequestBody QuizPlayerAnswers playerAnswers) {
		playerAnswers.setScore(0);
		playerAnswers.setCompletionTime(0);
		int score = 0;
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		try {
			player = playerService.getPlayerById(playerAnswers.getPlayer_id());
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player does not exists");
		}
		playerAnswers.setCompletionTime(System.currentTimeMillis() - quiz.getQuizStartDate().getTime());

		if (quiz.getPlayers().contains(player)) {
			if (quiz.getQuizEndDate() == null
					|| (System.currentTimeMillis() - quiz.getQuizEndDate().getTime()) < (1000 * 60 * 5)) {
				for (QuizPlayerAnswers qpa : quiz.getQuizPlayerAnswers()) {
					if (qpa.getPlayer_id() == playerAnswers.getPlayer_id()) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player already answered");
					}
				}
				/*all of this should be in a thread before the check if the player already answer, then down below
				 * should add t1.join to wait for the score, it should save time!!!!
				 * 
				 * for (Question q : quiz.getQuestions()) { if (q.getCorrectAnswerId() ==
				 * playerAnswers.getPlayerAnswers().get(q.getId())) {
				 * System.out.println("question id: " + q.getId() +
				 * " the answer is correct and its id is: " +
				 * playerAnswers.getPlayerAnswers().get(q.getId())); score++; } }
				 * 
				 * t1.join here!!!!!
				 */
				playerAnswers.setScore(score);
				quiz.getQuizPlayerAnswers().add(playerAnswers);
				try {
					quizService.updateQuiz(quiz);
					quizService.updateQuizWinnerPlayer(quiz.getId(),
							quiz.getPlayers().get(quiz.getPlayers().indexOf(player)), score);
				}catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
				}catch (InvalidInputException e1) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz or Player info is Invalid");
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanks your score is: " + score);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Time to answer the Quiz has passed");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You dont belong to this Quiz");
		}
	}

	@PostMapping("/join/{quizId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quiz_id, @RequestBody Player player) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.isQuizPrivate() == false) {
			quiz.getPlayers().add(player);
			try {
				quizService.updateQuiz(quiz);
			}catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Joined the Quiz");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Quiz is private, only the Quiz manager can add you");
		}
	}

	@PostMapping("/leave/{quizId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quiz_id, @RequestBody Player player) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getPlayers().remove(player)) {
			try {
				quizService.updateQuiz(quiz);
			}catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
			} catch (InvalidInputException e1) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Left the Quiz");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You dont belong to this Quiz");
		}
	}

	//player info validation need to be in the security microservice!
	@PutMapping("/updatePlayerInfo")
	public ResponseEntity<?> updatePlayerInfo(@RequestBody Player newPlayerInfo) {
		try {
			player = playerService.getPlayerById(newPlayerInfo.getId());
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player does not exists");
		}
		try {
			playerService.updatePlayer(newPlayerInfo);
		}catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player does not exists");
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player info is Invalid");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Player updated");
	}
	
	@PostMapping("/suggestQuestion/{player_id}")
	public ResponseEntity<?> suggestQuestion(@PathVariable long player_id, @RequestBody Question question){
		SuggestedQuestion suggestedQuestion = new SuggestedQuestion(0, player_id, question);
		try {
			suggestedQuestionService.addSuggestedQuestion(suggestedQuestion);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggested question input is invalid");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Question suggested");
	}
	
	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		try {
			List<Question> questions = (List<Question>) Hibernate.unproxy(questionService.getAllQuestions());
			return ResponseEntity.status(HttpStatus.OK).body(questions);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no Quizs");
		}
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(@PathVariable byte numberOfRandomQuestions) {
		List<Question> questions = questionService.getAllQuestions();
		List<Question> randomQuestions = new ArrayList<Question>();
		if (questions.size() <= numberOfRandomQuestions) {
			return ResponseEntity.status(HttpStatus.OK).body((List<Question>) Hibernate.unproxy(questions));
		} else {
			while (randomQuestions.size() < numberOfRandomQuestions) {
				randomQuestions.add(questions.get(Math.max(1, ((int) Math.random() * questions.size()))));
			}
			return ResponseEntity.status(HttpStatus.OK).body(randomQuestions);
		}
	}

	private long createQuizId() {
		long quizId;
		do {
			quizId = (long) Math.abs((Math.random() * 1000000000000000000l));
		} while (quizIdService.ifExistsById(quizId) && quizId <= 100000000000000000l);

		QuizId tempQuizId = new QuizId(quizId);
		try {
			quizIdService.addQuizId(tempQuizId);
			return quizId;
		} catch (EntityExistsException e) {
			return 0;
		}
	}

}
