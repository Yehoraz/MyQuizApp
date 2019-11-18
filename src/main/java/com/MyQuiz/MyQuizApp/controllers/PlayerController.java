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
	private Thread quizCopyThread = null;
	
	private final int MAX_TIME_PAST_QUIZ_END = 1000 * 10 ;

	@PostMapping("/createQuiz")
	public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz) {
		quiz.setId(createQuizId());
		if (quiz.getId() > 0) {
			try {
				quizService.addQuiz(quiz);
			} catch (EntityExistsException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz already exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz info is Invalid");
			}
			quizCopyThread = new Thread(new QuizCopyThread(quiz, quizCopyService));
			quizCopyThread.start();
			return ResponseEntity.status(HttpStatus.OK).body("Quiz Added");
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Server had a problem please try again");
		}
	}

	// urgent!!! need to check if everything (stream and lambda) works good!!! test
	// for exceptions!!!
	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quizId, @RequestBody QuizPlayerAnswers playerAnswers) {
		int score = 0;
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			player = playerService.getPlayerById(playerAnswers.getPlayerId());
			if (player != null) {
				playerAnswers.setCompletionTime(playerAnswers.getCompletionTime() - quiz.getQuizStartDate().getTime());
				if (quiz.getPlayers().contains(player)) {
					if (quiz.getQuizEndDate() == null
							|| (System.currentTimeMillis() - quiz.getQuizEndDate().getTime()) < MAX_TIME_PAST_QUIZ_END) {
						if (quiz.getQuizPlayerAnswers().stream()
								.filter(p -> p.getPlayerId() == playerAnswers.getPlayerId()).count() > 0) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player already answered");
						}
//						for (QuizPlayerAnswers qpa : quiz.getQuizPlayerAnswers()) {
//							if (qpa.getPlayerId() == playerAnswers.getPlayerId()) {
//							}
//						}
						score = (int) quiz.getQuestions().stream()
								.filter(q -> q.getCorrectAnswerId() == playerAnswers.getPlayerAnswers().get(q.getId()))
								.count();
						System.out.println("the score is: " + score);
//						for (Question q : quiz.getQuestions()) {
//							if (q.getCorrectAnswerId() == playerAnswers.getPlayerAnswers().get(q.getId())) {
//								System.out
//										.println("question id: " + q.getId() + " the answer is correct and its id is: "
//												+ playerAnswers.getPlayerAnswers().get(q.getId()));
//								score++;
//							}
//						}

						playerAnswers.setScore(score);
						quiz.getQuizPlayerAnswers().add(playerAnswers);
						try {
							if (quiz.getWinnerPlayerScore() < score) {
								quiz.setWinnerPlayer(player);
								quiz.setWinnerPlayerScore(score);
							}
							quizService.updateQuiz(quiz);
						} catch (EntityNotFoundException e) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
						} catch (InvalidInputException e1) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz or Player info is Invalid");
						}
						return ResponseEntity.status(HttpStatus.OK).body("Thanks your score is: " + score);
					} else {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Time to answer the Quiz has passed");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("You dont belong to this Quiz");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PostMapping("/join/{quizId}/{playerId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quizId, @PathVariable long playerId) {
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.isQuizPrivate() == false) {
				if (quiz.getQuizEndDate() == null) {
					player = playerService.getPlayerById(playerId);
					if (player != null) {
						quiz.getPlayers().add(player);
						try {
							quizService.updateQuiz(quiz);
						} catch (EntityNotFoundException e) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
						} catch (InvalidInputException e) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz info is Invalid");
						}
						return ResponseEntity.status(HttpStatus.OK).body("Joined the Quiz");
					} else {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Can not join an ended quiz");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("Quiz is private, only the Quiz manager can add you");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PostMapping("/leave/{quizId}/{playerId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quizId, @PathVariable long playerId) {
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			player = playerService.getPlayerById(playerId);
			if (player != null) {
				if (quiz.getPlayers().remove(player)) {
					try {
						quizService.updateQuiz(quiz);
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
					} catch (InvalidInputException e1) {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz info is Invalid");
					}
					return ResponseEntity.status(HttpStatus.OK).body("You left the Quiz");
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("You dont belong to this Quiz");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PutMapping("/updatePlayerInfo")
	public ResponseEntity<?> updatePlayerInfo(@RequestBody Player newPlayerInfo) {
		player = playerService.getPlayerById(newPlayerInfo.getId());
		if (player != null) {
			try {
				playerService.updatePlayer(newPlayerInfo);
			} catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Player updated");
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
		}
	}

	@PostMapping("/suggestQuestion/{playerId}")
	public ResponseEntity<?> suggestQuestion(@PathVariable long playerId, @RequestBody Question question) {
		suggestedQuestionService.addSuggestedQuestion(new SuggestedQuestion(0, playerId, question));
		return ResponseEntity.status(HttpStatus.OK).body("Question suggested");
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		try {
			List<Question> questions = (List<Question>) Hibernate.unproxy(questionService.getAllQuestions());
			return ResponseEntity.status(HttpStatus.OK).body(questions);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(@PathVariable byte numberOfRandomQuestions) {
		List<Question> questions;
		try {
			questions = questionService.getAllQuestions();
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
		if (questions.size() <= numberOfRandomQuestions) {
			return ResponseEntity.status(HttpStatus.OK).body((List<Question>) Hibernate.unproxy(questions));
		} else {
			List<Question> randomQuestions = new ArrayList<Question>();
			while (randomQuestions.size() < numberOfRandomQuestions) {
				randomQuestions.add(questions.get(Math.max(1, ((int) Math.random() * questions.size()))));
			}
			return ResponseEntity.status(HttpStatus.OK).body(randomQuestions);
		}
	}

	@PostMapping("/addPlayer")
	public ResponseEntity<?> addPlayer(@RequestBody Player player) {
		try {
			playerService.addPlayer(player);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		} catch (EntityExistsException e) {
			return ResponseEntity.status(HttpStatus.CREATED).body(null);
		}
	}

	@GetMapping("/getWinner/{quizId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quizId) {
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizEndDate() != null
					&& ((System.currentTimeMillis() - quiz.getQuizEndDate().getTime()) > MAX_TIME_PAST_QUIZ_END)) {
				return ResponseEntity.status(HttpStatus.OK).body(quiz.getWinnerPlayer().getFirstName() + " "
						+ quiz.getWinnerPlayer().getLastName() + " won with score of: " + quiz.getWinnerPlayerScore());
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz not finished yet");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	private long createQuizId() {
		long quizId;
		do {
			quizId = (long) Math.abs((Math.random() * 1000000000000000000l));
		} while (quizService.ifExistsById(quizId));
		return quizId;
	}

}
