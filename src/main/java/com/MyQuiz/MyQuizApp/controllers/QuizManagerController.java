package com.MyQuiz.MyQuizApp.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizId;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.services.AnswerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;
import com.MyQuiz.MyQuizApp.services.QuizIdService;
import com.MyQuiz.MyQuizApp.services.QuizService;

@RestController
public class QuizManagerController {

	// must create check methods in service/facade!!!!!!

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizCopyService quizCopyService;

	@Autowired
	private QuizIdService quizIdService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private AnswerService answerService;

	private Quiz quiz = null;
	private QuizCopy quizCopy = null;
	private Question question = null;
	private Answer answer = null;

	@PutMapping("/startQuiz/{quizId}/{startTime}/{quizManagerId}")
	public ResponseEntity<?> startQuiz(@PathVariable long quiz_id, @PathVariable long startTime,
			@PathVariable long quizManagerId) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			if (quiz.getQuizStartDate() == null) {
				quiz.setQuizStartDate(new Date(startTime));
				try {
					quizService.updateQuiz(quiz);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
				} catch (InvalidInputException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
				}
				return ResponseEntity.status(HttpStatus.OK).body("Quiz started");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already started");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@PutMapping("/stopQuiz/{quizId}/{endTime}/{quizManagerId}")
	public ResponseEntity<?> stopQuiz(@PathVariable long quiz_id, @PathVariable long endTime,
			@PathVariable long quizManagerId) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizStartDate().getTime() < endTime) {
			if (quiz.getQuizManager().getId() == quizManagerId) {
				// exception, request content was messed up
				if (quiz.getQuizEndDate() == null) {
					quiz.setQuizEndDate(new Date(endTime));
					try {
						quizService.updateQuiz(quiz);
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
					} catch (InvalidInputException e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
					}
					return ResponseEntity.status(HttpStatus.OK).body("Quiz stoped");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already stoped");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
			}
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request data is invalid");
		}
	}

	@GetMapping("/getWinner/{quizId}/{quizManagerId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quiz_id, @PathVariable long quizManagerId) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			if (quiz.getQuizEndDate() != null
					&& ((System.currentTimeMillis() - quiz.getQuizEndDate().getTime()) > (1000 * 60 * 5))) {
				return ResponseEntity.status(HttpStatus.OK).body(quiz.getWinnerPlayer().getFirstName() + " "
						+ quiz.getWinnerPlayer().getLastName() + " won with score of: " + quiz.getWinnerPlayerScore());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz not finished yet");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@PostMapping("/addPlayer/{quizId}/{quizManagerId}")
	public ResponseEntity<?> addPlayerToPrivateQuiz(@PathVariable long quiz_id, @PathVariable long quizManagerId,
			@RequestBody Player player) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			quiz.getPlayers().add(player);
			try {
				quizService.updateQuiz(quiz);
			} catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Player added");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@PutMapping("/updateAnswer/{quizId}/{questionId}/{answerId}/{quizManagerId}")
	public ResponseEntity<?> updateAnswer(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable int answer_id, @PathVariable long quizManagerId, @RequestBody String answerText) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			if (quiz.getQuizStartDate() == null) {
				try {
					question = questionService.getQuestionById(question_id);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question does not exists");
				}
				try {
					answer = answerService.getAnswerById(answer_id);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Answer does not exists");
				}
				quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).getAnswers().get(
						quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).getAnswers().indexOf(answer))
						.setAnswerText(answerText);
				try {
					quizService.updateQuiz(quiz);
					quizCopy = quizCopyService.getQuizCopy(quiz.getId());
					quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question)).getAnswers().get(quizCopy
							.getQuestions().get(quizCopy.getQuestions().indexOf(question)).getAnswers().indexOf(answer))
							.setAnswerText(answerText);
					quizCopyService.updateQuizCopy(quizCopy);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
				} catch (InvalidInputException e1) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
				}
				return ResponseEntity.status(HttpStatus.OK).body("Answer updated");

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already started");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@PutMapping("/updateQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> updateQuestion(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable long quizManagerId, @RequestBody String questionText) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			if (quiz.getQuizStartDate() == null) {
				try {
					question = questionService.getQuestionById(question_id);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question does not exists");
				}
				quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).setQuestionText(questionText);
				try {
					quizService.updateQuiz(quiz);
					quizCopy = quizCopyService.getQuizCopy(quiz_id);
					quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question))
							.setQuestionText(questionText);
					quizCopyService.updateQuizCopy(quizCopy);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
				} catch (InvalidInputException e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
				}
				return ResponseEntity.status(HttpStatus.OK).body("Question updated");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already started");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@DeleteMapping("/removeQuestion/{quizId}/{questionNumber}/{quizManagerId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable long quizManagerId) {
		try {
			quiz = quizService.getQuizById(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		if (quiz.getQuizManager().getId() == quizManagerId) {
			try {
				question = questionService.getQuestionById(question_id);
			} catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question does not exists");
			}
			quiz.getQuestions().remove(question);
			try {
				quizService.updateQuiz(quiz);
				quizCopy = quizCopyService.getQuizCopy(quiz_id);
				quizCopy.getQuestions().remove(question);
				quizCopyService.updateQuizCopy(quizCopy);
			} catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Question removed");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

	@DeleteMapping("/removeQuiz/{quizId}/{quizManagerId}")
	public ResponseEntity<?> removeQuiz(@PathVariable long quiz_id, @PathVariable long quizManagerId) {
		try {
			quiz = quizService.getQuizById(quiz_id);
			quizCopy = quizCopyService.getQuizCopy(quiz_id);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
		}
		QuizId quizId = new QuizId(quiz.getId());
		if (quiz.getQuizManager().getId() == quizManagerId) {
			// need to remove quiz manager role here!!! need to fix!
			try {
				quizService.removeQuiz(quiz);
				quizCopyService.removeQuizCopy(quizCopy);
				quizIdService.removeQuizId(quizId);
			} catch (EntityNotFoundException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz does not exists");
			} catch (InvalidInputException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz info is Invalid");
			}
			return ResponseEntity.status(HttpStatus.OK).body("Quiz removed");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the Quiz manager");
		}
	}

}
