package com.MyQuiz.MyQuizApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.enums.QuizExceptionType;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.NotExistsException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.exceptions.QuizServerException;
import com.MyQuiz.MyQuizApp.services.QuizManagerService;

@RestController
public class QuizManagerController {

	@Autowired
	private QuizManagerService quizManagerService;

	@PutMapping("/startQuiz/{quizId}/{startTime}/{quizManagerId}")
	public ResponseEntity<?> startQuiz(@PathVariable long quizId, @PathVariable long startTime,
			@PathVariable long quizManagerId) {
		try {
			quizManagerService.startQuiz(quizId, startTime, quizManagerId);
			// push notification with QuizCopy need to be sent!
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.QuizStarted)) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else if (e.getExceptionType().equals(QuizExceptionType.NotQuizManager)) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PutMapping("/stopQuiz/{quizId}/{endTime}/{quizManagerId}")
	public ResponseEntity<?> stopQuiz(@PathVariable long quizId, @PathVariable long endTime,
			@PathVariable long quizManagerId) {
		try {
			quizManagerService.stopQuiz(quizId, endTime, quizManagerId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@PutMapping("/addPlayerToQuiz/{quizId}/{playerId}/{quizManagerId}")
	public ResponseEntity<?> addPlayerToPrivateQuiz(@PathVariable long quizId, @PathVariable long playerId,
			@PathVariable long quizManagerId) {
		try {
			quizManagerService.addPlayerToPrivateQuiz(quizId, playerId, quizManagerId);
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
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@PutMapping("/updateAnswer/{quizId}/{questionId}/{answerId}/{quizManagerId}")
	public ResponseEntity<?> updateAnswer(@PathVariable long quizId, @PathVariable long questionId,
			@PathVariable long answerId, @PathVariable long quizManagerId, @RequestBody String answerText) {
		try {
			quizManagerService.updateAnswer(quizId, questionId, answerId, quizManagerId, answerText);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (NotExistsException e) {
			if (e.getId() == quizId) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			} else if (e.getId() == questionId) {
				return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body(null);
			} else if (e.getId() == answerId) {
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.QuizStarted)) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else if (e.getExceptionType().equals(QuizExceptionType.NotQuizManager)) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@PutMapping("/updateQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> updateQuestion(@PathVariable long quizId, @PathVariable long questionId,
			@PathVariable long quizManagerId, @RequestBody String questionText) {
		try {
			quizManagerService.updateQuestion(quizId, questionId, quizManagerId, questionText);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (NotExistsException e) {
			if (e.getId() == quizId) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			} else if (e.getId() == questionId) {
				return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.QuizStarted)) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else if (e.getExceptionType().equals(QuizExceptionType.NotQuizManager)) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@PutMapping("/addQuestionToQuiz/{quizManagerId}")
	public ResponseEntity<?> addQuestionToQuiz(@PathVariable long quizManagerId, @RequestBody Question question) {
		try {
			quizManagerService.addQuestionToQuiz(quizManagerId, question);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@DeleteMapping("/removeQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long quizId, @PathVariable long questionId,
			@PathVariable long quizManagerId) {
		try {
			quizManagerService.removeQuestion(quizId, questionId, quizManagerId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (NotExistsException e) {
			if (e.getId() == quizId) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			} else if (e.getId() == questionId) {
				return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (QuizException e) {
			if (e.getExceptionType().equals(QuizExceptionType.NotQuizQuestion)) {
				return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body(null);
			} else if (e.getExceptionType().equals(QuizExceptionType.NotQuizManager)) {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	//need to check this service method for explanation about what to do with it!
	@DeleteMapping("/removeQuiz/{quizId}/{quizManagerId}")
	public ResponseEntity<?> removeQuiz(@PathVariable long quizId, @PathVariable long quizManagerId) {
		try {
			quizManagerService.removeQuiz(quizId, quizManagerId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (QuizServerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/getOnGoingQuiz/{quizManagerId}")
	public ResponseEntity<?> getOnGoingQuiz(@PathVariable long quizManagerId) {
		Quiz quiz = quizManagerService.getOnGoingQuiz(quizManagerId);
		if (quiz != null) {
			return ResponseEntity.status(HttpStatus.OK).body(quiz);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

	@GetMapping("/getQuizForEdit/{quizManagerId}")
	public ResponseEntity<?> getQuizForEdit(@PathVariable long quizManagerId) {
		Quiz quiz = quizManagerService.getQuizForEdit(quizManagerId);
		if (quiz != null) {
			return ResponseEntity.status(HttpStatus.OK).body(quiz);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
	}

}
