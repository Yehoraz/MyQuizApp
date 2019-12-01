package com.MyQuiz.MyQuizApp.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

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
import com.MyQuiz.MyQuizApp.beans.PlayerMongo;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizInfo;
import com.MyQuiz.MyQuizApp.services.AnswerService;
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;
import com.MyQuiz.MyQuizApp.services.QuizInfoService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@RestController
public class QuizManagerController {

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

	@Autowired
	private QuizInfoService quizInfoService;

	private Quiz quiz = null;
	private QuizCopy quizCopy = null;
	private Question question = null;
	private Answer answer = null;
	private QuizInfo quizInfo = null;
	private List<PlayerMongo> playersMongo = null;

	@PutMapping("/startQuiz/{quizId}/{startTime}/{quizManagerId}")
	public ResponseEntity<?> startQuiz(@PathVariable long quizId, @PathVariable long startTime,
			@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				if (quiz.getQuizStartDate() == null) {
					quiz.setQuizStartDate(new Date(startTime));
					quiz.setQuizEndDate(new Date(startTime + quiz.getQuizMaxTimeInMillis()));
					try {
						quizService.updateQuiz(quiz);
						// push notification with QuizCopy need to be sent!
						return ResponseEntity.status(HttpStatus.OK).body(null);
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
					}
				} else {
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PutMapping("/stopQuiz/{quizId}/{endTime}/{quizManagerId}")
	public ResponseEntity<?> stopQuiz(@PathVariable long quizId, @PathVariable long endTime,
			@PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				if (quiz.getQuizStartDate().getTime() < endTime && quiz.getQuizEndDate().getTime() > endTime) {
					quiz.setQuizEndDate(new Date(endTime));
					try {
						quizService.updateQuiz(quiz);
						QuizCopy quizCopy = quizCopyService.getQuizCopy(quiz.getId());
						quizCopyService.removeQuizCopy(quizCopy);
						return ResponseEntity.status(HttpStatus.OK).body(null);
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
					}
				} else {
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
						return ResponseEntity.status(HttpStatus.OK).body("Player added");
					} catch (EntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
					}
				} else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Something went wrong");
				}
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Only the quiz manager can add players");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PutMapping("/updateAnswer/{quizId}/{questionId}/{answerId}/{quizManagerId}")
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
								//need to check if this works because Quiz question and quizcopy question are diffrent!!!
								quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question)).getAnswers()
										.get(quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question))
												.getAnswers().indexOf(answer))
										.setAnswerText(answerText);
								quizCopyService.updateQuizCopy(quizCopy);
								return ResponseEntity.status(HttpStatus.OK).body(null);
							} catch (EntityNotFoundException e) {
								return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
							}
						} else {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
						}
					} else {
						return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body(null);
					}
				} else {
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PutMapping("/updateQuestion/{quizId}/{questionId}/{quizManagerId}")
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
							//need to check if this works because Quiz question and quizcopy question are diffrent!!!
							quizCopy.getQuestions().get(quizCopy.getQuestions().indexOf(question))
									.setQuestionText(questionText);
							quizCopyService.updateQuizCopy(quizCopy);
							return ResponseEntity.status(HttpStatus.OK).body("Question updated");
						} catch (EntityNotFoundException e) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
						}
					} else {
						return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body("Question does not exists");
					}
				} else {
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@DeleteMapping("/removeQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long quizId, @PathVariable int questionId,
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
							return ResponseEntity.status(HttpStatus.OK).body(null);
						} catch (EntityNotFoundException e) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
						}
					} else {
						return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).body(null);
					}
				} else {
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@DeleteMapping("/removeQuiz/{quizId}/{quizManagerId}")
	public void removeQuiz(@PathVariable long quizId, @PathVariable long quizManagerId) {
		restartVariables();
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizManagerId() == quizManagerId) {
				try {
					playersMongo = new ArrayList<PlayerMongo>();
					quiz.getPlayers().forEach(p -> playersMongo
							.add(new PlayerMongo(p.getId(), p.getFirstName(), p.getLastName(), p.getAge())));
					quizInfo = new QuizInfo(quiz.getId(), quiz.getQuizName(), quiz.getWinnerPlayer().getId(),
							playersMongo);
					quizInfoService.addQuizInfo(quizInfo);
					quizService.removeQuiz(quiz);
					quizCopy = quizCopyService.getQuizCopy(quizId);
					if (quizCopy != null) {
						quizCopyService.removeQuizCopy(quizCopy);
					}
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

	private void restartVariables() {
		quiz = null;
		quizCopy = null;
		question = null;
		answer = null;
		quizInfo = null;
		playersMongo = null;
	}

}
