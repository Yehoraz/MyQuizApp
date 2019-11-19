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

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.services.SuggestedQuestionService;
import com.MyQuiz.MyQuizApp.threads.QuizCopyThread;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@RestController
public class PlayerController {

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizCopyService quizCopyService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private SuggestedQuestionService suggestedQuestionService;

	@Autowired
	private QuestionService questionService;

	private Quiz quiz = null;
	private Player player = null;
	private Thread quizCopyThread = null;

	private final int MAX_TIME_PAST_QUIZ_END = 1000 * 10;

	@PostMapping("/createQuiz")
	public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz) {
		quiz.setId(createQuizId());
		if (ValidationUtil.validationCheck(quiz)) {
			try {
				quizService.addQuiz(quiz);
			} catch (EntityExistsException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("Server Error please try again later or contact us");
			}
			long id = quiz.getId();
			quiz = null;
			quiz = quizService.getQuizById(id);
			if (quiz != null) {
				//need to check this lambda!!!!
				quiz.getQuestions().forEach(q-> q.getAnswers().forEach(a-> {if(a.isCorrectAnswer()) {q.setCorrectAnswerId(a.getId());}}));
				//if lambda work should delete this for loop!!
				for(Question q: quiz.getQuestions()) {
					for(Answer a: q.getAnswers()) {
						if(a.isCorrectAnswer()) {
							q.setCorrectAnswerId(a.getId());
							break;
						}
					}
				}
				quizCopyThread = new Thread(new QuizCopyThread(quiz, quizCopyService));
				quizCopyThread.start();
				return ResponseEntity.status(HttpStatus.OK).body("Quiz Added");
			}else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Server error please try again later or contact us");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	// urgent!!! need to check if everything (stream and lambda) works good!!! test
	// for exceptions!!!
	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quizId, @RequestBody QuizPlayerAnswers playerAnswers) {
		int score = 0;
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (ValidationUtil.validationCheck(playerAnswers)) {
				player = playerService.getPlayerById(playerAnswers.getPlayerId());
				if (player != null) {
					playerAnswers
							.setCompletionTime(playerAnswers.getCompletionTime() - quiz.getQuizStartDate().getTime());
					if (quiz.getPlayers().contains(player)) {
						if (quiz.getQuizEndDate() == null || (System.currentTimeMillis()
								- quiz.getQuizEndDate().getTime()) < MAX_TIME_PAST_QUIZ_END) {
							if (quiz.getQuizPlayerAnswers().stream()
									.filter(p -> p.getPlayerId() == playerAnswers.getPlayerId()).count() > 0) {
								return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player already answered");
							} else {
								score = (int) quiz.getQuestions().stream().filter(
										q -> q.getCorrectAnswerId() == playerAnswers.getPlayerAnswers().get(q.getId()))
										.count();
								System.out.println("the score is: " + score);
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
								}
								return ResponseEntity.status(HttpStatus.OK).body("Thanks your score is: " + score);
							}
						} else {
							return ResponseEntity.status(HttpStatus.ACCEPTED)
									.body("Time to answer the Quiz has passed");
						}
					} else {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("You dont belong to this Quiz");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
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

	@PostMapping("/suggestQuestion/{playerId}")
	public ResponseEntity<?> suggestQuestion(@PathVariable long playerId, @RequestBody Question question) {
		if (ValidationUtil.validationCheck(question)) {
			try {
				suggestedQuestionService.addSuggestedQuestion(new SuggestedQuestion(0, playerId, question));
			} catch (EntityExistsException e) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Question already suggested");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Question suggested");
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	@PutMapping("/updateSuggestedQuestion/{sqID}/{playerId}")
	public ResponseEntity<?> updateSuggestedQuestion(@PathVariable("sqID") long sQuestionId,
			@PathVariable long playerId) {
		SuggestedQuestion sQuestion = suggestedQuestionService.getSuggestedQuestion(sQuestionId);
		if (sQuestion != null) {
			if (sQuestion.getPlayerId() == playerId) {
				if (ValidationUtil.validationCheck(sQuestion)) {
					suggestedQuestionService.updateSuggestedQuestion(sQuestion);
					return ResponseEntity.status(HttpStatus.OK).body("Suggested Question updated");
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("Only the player who suggested the question can update it");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Suggested question does not exists");
		}
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

	private long createQuizId() {
		long quizId;
		do {
			quizId = (long) Math.abs((Math.random() * 1000000000000000000l));
		} while (quizService.ifExistsById(quizId));
		return quizId;
	}

}
