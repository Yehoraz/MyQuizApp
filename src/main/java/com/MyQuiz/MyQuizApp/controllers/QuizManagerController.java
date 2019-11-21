package com.MyQuiz.MyQuizApp.controllers;

import java.sql.Date;
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
import com.MyQuiz.MyQuizApp.services.AnswerService;
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@RestController
public class QuizManagerController {

	// must make a check method for each quiz, if the max time limit has reached
	// then quiz got to stop by its own!!!!!!!!!

	// must create check methods in service/facade!!!!!!

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizCopyService quizCopyService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private AnswerService answerService;

	@Autowired
	private PlayerService playerService;

	private Quiz quiz = null;
	private QuizCopy quizCopy = null;
	private Question question = null;
	private Answer answer = null;

	@PutMapping("/startQuiz/{quizId}/{startTime}/{quizManagerId}")
	public void startQuiz(@PathVariable long quizId, @PathVariable long startTime, @PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				if (quiz.getQuizStartDate() == null) {
					quiz.setQuizStartDate(new Date(startTime));
					try {
						quizService.updateQuiz(quiz);
					} catch (EntityNotFoundException e) {
						// logger
					}
					// logger
				} else {
					// logger
				}
			} else {
				// logger
			}
		} else {
			// logger
		}
	}

	@PutMapping("/stopQuiz/{quizId}/{endTime}/{quizManagerId}")
	public void stopQuiz(@PathVariable long quizId, @PathVariable long endTime, @PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizEndDate() != null) {
				if (quiz.getQuizManagerId() == quizManagerId) {
					if (quiz.getQuizStartDate().getTime() < endTime) {
						quiz.setQuizEndDate(new Date(endTime));
						try {
							quizService.updateQuiz(quiz);
							QuizCopy quizCopy = quizCopyService.getQuizCopy(quiz.getId());
							quizCopyService.removeQuizCopy(quizCopy);
						} catch (EntityNotFoundException e) {
							// logger
						}
						// logger
					} else {
						// logger
					}
				} else {
					// logger
				}
			} else {
				// logger
			}
		} else {
			// logger
		}
	}

	@PostMapping("/addPlayerToQuiz/{quizId}/{playerId}/{quizManagerId}")
	public ResponseEntity<?> addPlayerToPrivateQuiz(@PathVariable long quizId, @PathVariable long playerId,
			@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				Player player = playerService.getPlayerById(playerId);
				if (player != null) {
					quiz.getPlayers().add(player);
					try {
						quizService.updateQuiz(quiz);
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
					}
					return ResponseEntity.status(HttpStatus.OK).body("Player added");
				} else {
					return ResponseEntity.status(HttpStatus.CREATED).body("Something went wrong");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("You are not the Quiz manager");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PostMapping("/updateAnswer/{quizId}/{questionId}/{answerId}/{quizManagerId}")
	public ResponseEntity<?> updateAnswer(@PathVariable long quizId, @PathVariable int questionId,
			@PathVariable int answerId, @PathVariable long quizManagerId, @RequestBody String answerText) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				if (quiz.getQuizStartDate() == null) {
					question = questionService.getQuestionById(questionId);
					if (question != null) {
						answer = answerService.getAnswerById(answerId);
						if (answer != null) {
							System.out.println("this is a check at main Quiz app in updateAnswer rest method!!!");
							System.out.println(quiz.getQuestions().stream()
									.map(q -> q.getAnswers().stream().filter(a -> a.getId() == answerId).findFirst()));

							quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).getAnswers()
									.get(quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).getAnswers()
											.indexOf(answer))
									.setAnswerText(answerText);
							try {
								quizService.updateQuiz(quiz);
								quizCopy = quizCopyService.getQuizCopy(quiz.getId());
								quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question)).getAnswers()
										.get(quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question))
												.getAnswers().indexOf(answer))
										.setAnswerText(answerText);
								quizCopyService.updateQuizCopy(quizCopy);
							} catch (EntityNotFoundException e) {
								return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
							}
							return ResponseEntity.status(HttpStatus.OK).body("Answer updated");
						} else {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Answer does not exists");
						}
					} else {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Question does not exists");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz already started");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("You are not the Quiz manager");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PostMapping("/updateQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> updateQuestion(@PathVariable long quizId, @PathVariable int questionId,
			@PathVariable long quizManagerId, @RequestBody String questionText) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				if (quiz.getQuizStartDate() == null) {
					question = questionService.getQuestionById(questionId);
					if (question != null) {
						quiz.getQuestions().get(quiz.getQuestions().indexOf(question)).setQuestionText(questionText);
						try {
							quizService.updateQuiz(quiz);
							quizCopy = quizCopyService.getQuizCopy(quizId);
							quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question))
									.setQuestionText(questionText);
							quizCopyService.updateQuizCopy(quizCopy);
						} catch (EntityNotFoundException e) {
							return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
						}
						return ResponseEntity.status(HttpStatus.OK).body("Question updated");
					} else {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Question does not exists");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz already started");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("You are not the Quiz manager");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@DeleteMapping("/removeQuestion/{quizId}/{questionId}/{quizManagerId}")
	public void removeQuestion(@PathVariable long quizId, @PathVariable int questionId,
			@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				question = questionService.getQuestionById(questionId);
				if (question != null) {
					if (quiz.getQuestions().remove(question)) {
						try {
							quizService.updateQuiz(quiz);
							quizCopy = quizCopyService.getQuizCopy(quizId);
							// need to check if it finds the Question object in the quizcopy because the
							// answers are not the same for quiz Question object and quizcopy Question
							// object
							quizCopy.getQuestions().remove(question);
							quizCopyService.updateQuizCopy(quizCopy);
						} catch (EntityNotFoundException e) {
							// logger
						}
						// logger
					} else {
						// logger
					}
				} else {
					// logger
				}
			} else {
				// logger
			}
		} else {
			// logger
		}
	}

	@DeleteMapping("/removeQuiz/{quizId}/{quizManagerId}")
	public void removeQuiz(@PathVariable long quizId, @PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				try {
					quizService.removeQuiz(quiz);
					quizCopy = quizCopyService.getQuizCopy(quizId);
					quizCopyService.removeQuizCopy(quizCopy);
				} catch (EntityNotFoundException e) {
					// logger
				}
			} else {
				// logger
			}
		} else {
			// logger
		}
	}

	@GetMapping("/getOnGoingQuiz/{quizManagerId}")
	public ResponseEntity<?> getOnGoingQuiz(@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizStartByManagerId(quizManagerId);
		if (quiz != null) {
			return ResponseEntity.status(HttpStatus.OK).body(quiz);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body("You do not manage any on going quiz");
		}
	}

	@GetMapping("/getQuizForEdit/{quizManagerId}")
	public ResponseEntity<?> getQuizForEdit(@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizOpenByManagerId(quizManagerId);
		if (quiz != null) {
			return ResponseEntity.status(HttpStatus.OK).body(quiz);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body("You do not manage any open quiz or quiz has started");
		}
	}

	@GetMapping("/getAllPrevQuizs/{quizManagerId}")
	public ResponseEntity<?> getAllPrevQuizs(@PathVariable long quizManagerId) {
		List<Quiz> quizs = quizService.getAllPrevQuizs(quizManagerId);
		if (quizs != null) {
			return ResponseEntity.status(HttpStatus.OK).body((List<Quiz>) Hibernate.unproxy(quizs));
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("You have never managed any quiz");
		}
	}

	@PostMapping("/addQuestionToQuiz/{quizManagerId}")
	public ResponseEntity<?> addQuestionToQuiz(@PathVariable long quizManagerId, @RequestBody Question question) {
		restartVariables();
		if (ValidationUtil.validationCheck(question)) {
			question.setApproved(false);
			quiz = quizService.getQuizOpenByManagerId(quizManagerId);
			if (quiz != null) {
				quiz.getQuestions().add(question);
				quizService.updateQuiz(quiz);
				return ResponseEntity.status(HttpStatus.OK).body("Question added");
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("You do not manage any open quiz or quiz has started");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	// missing method!!!
	// get all quiz questions so the manager could update, remove and add
	// questions!!

	private void restartVariables() {
		quiz = null;
		quizCopy = null;
		question = null;
		answer = null;
	}

}
