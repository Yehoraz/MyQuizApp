package com.MyQuiz.MyQuizApp.controllers;

import java.util.List;

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

import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizInfo;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.enums.QuizExceptionType;
import com.MyQuiz.MyQuizApp.exceptions.ExistsException;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.NotExistsException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.exceptions.QuizServerException;
import com.MyQuiz.MyQuizApp.services.PlayerService;

@RestController
public class PlayerController {

	@Autowired
	private PlayerService playerService;

	private String serverErrorMessage = "Server error please try again later or contact us";

	@PostMapping("/createQuiz")
	public ResponseEntity<String> createQuiz(@RequestBody Quiz quiz) {
		try {
			playerService.createQuiz(quiz);
			return ResponseEntity.status(HttpStatus.OK).body("Quiz Added");
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(serverErrorMessage);
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body("You can not have more than one Quiz open at the same time");
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	@PostMapping("/suggestQuestion/{playerId}")
	public ResponseEntity<?> suggestQuestion(@PathVariable long playerId, @RequestBody Question question) {
		try {
			playerService.suggestQuestion(playerId, question);
			return ResponseEntity.status(HttpStatus.OK).body("Question suggested");
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		} catch (ExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("This question already suggested");
		}
	}

	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quizId, @RequestBody QuizPlayerAnswers playerAnswers) {
		try {
			int score = playerService.answerQuiz(quizId, playerAnswers);
			return ResponseEntity.status(HttpStatus.OK).body("Thanks your score is: " + score);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(serverErrorMessage);
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.NotQuizPlayer)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("You don't belong to this Quiz");
			} else if (e.getExceptionType().equals(QuizExceptionType.QuizEnded)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Time to answer the Quiz has passed");
			} else if (e.getExceptionType().equals(QuizExceptionType.AlreadyAnswered)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player already answered");
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(serverErrorMessage);
			}
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(e.getMessage());
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	@PutMapping("/join/{quizId}/{playerId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quizId, @PathVariable long playerId) {
		try {
			playerService.joinQuiz(quizId, playerId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (NotExistsException e) {
			if (e.getId() == quizId) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			} else if (e.getId() == playerId) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.QuizEnded)) {
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
			} else if (e.getExceptionType().equals(QuizExceptionType.QuizIsPrivate)) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@PutMapping("/leave/{quizId}/{playerId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quizId, @PathVariable long playerId) {
		try {
			playerService.leaveQuiz(quizId, playerId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		} catch (NotExistsException e) {
			if (e.getId() == quizId) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			} else if (e.getId() == playerId) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@PutMapping("/updateSuggestedQuestion/{playerId}")
	public ResponseEntity<?> updateSuggestedQuestion(@PathVariable long playerId,
			@RequestBody SuggestedQuestion suggestedQuestion) {
		try {
			playerService.updateSuggestedQuestion(playerId, suggestedQuestion);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
		}
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		try {
			List<Question> questions = playerService.getAllQuestions();
			return ResponseEntity.status(HttpStatus.OK).body((List<Question>) Hibernate.unproxy(questions));
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(@PathVariable short numberOfRandomQuestions) {
		try {
			List<Question> questions = playerService.getRandomQuestions(numberOfRandomQuestions);
			return ResponseEntity.status(HttpStatus.OK).body((List<Question>) Hibernate.unproxy(questions));
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

	@GetMapping("/getQuizInfo/{quizId}")
	public ResponseEntity<?> getQuizInfo(@PathVariable long quizId) {
		try {
			QuizInfo quizInfo = playerService.getQuizInfo(quizId);
			return ResponseEntity.status(HttpStatus.OK).body(quizInfo);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

	@GetMapping("/getAllPrevQuizs/{playerId}")
	public ResponseEntity<?> getAllPrevQuizs(@PathVariable long playerId) {
		try {
			List<Quiz> quizs = playerService.getAllPrevQuizs(playerId);
			return ResponseEntity.status(HttpStatus.OK).body((List<Quiz>) Hibernate.unproxy(quizs));
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

}
