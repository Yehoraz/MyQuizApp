package com.MyQuiz.MyQuizApp.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.PlayerMongo;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizInfo;
import com.MyQuiz.MyQuizApp.enums.QuizExceptionType;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.NotExistsException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.exceptions.QuizServerException;
import com.MyQuiz.MyQuizApp.repos.AnswerRepository;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;
import com.MyQuiz.MyQuizApp.repos.QuestionRepository;
import com.MyQuiz.MyQuizApp.repos.QuizCopyRepository;
import com.MyQuiz.MyQuizApp.repos.QuizInfoRepository;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@Service
public class QuizManagerService {

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuizCopyRepository quizCopyRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private QuizInfoRepository quizInfoRepository;

//	@Autowired
//	private QuizService quizService;
//
//	@Autowired
//	private QuizCopyService quizCopyService;
//
//	@Autowired
//	private QuestionService questionService;
//
//	@Autowired
//	private AnswerService answerService;
//
//	@Autowired
//	private PlayerService playerService;
//
//	@Autowired
//	private QuizInfoService quizInfoService;

	private Quiz quizItem = null;
	private QuizCopy quizCopyItem = null;
	private Question questionItem = null;
	private Answer answerItem = null;
	private QuizInfo quizInfoItem = null;
	private Player playerItem = null;
	private List<PlayerMongo> playersMongoItem = null;

	public void startQuiz(long quizId, long startTime, long quizManagerId)
			throws QuizServerException, QuizException, NotExistsException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizManagerId() == quizManagerId) {
				if (quizItem.getQuizStartDate() == null) {
					quizItem.setQuizStartDate(new Date(startTime));
					quizItem.setQuizEndDate(new Date(startTime + quizItem.getQuizMaxTimeInMillis()));
					if (quizRepository.existsById(quizItem.getId())) {
						quizRepository.save(quizItem);
						// push notification with QuizCopy need to be sent!
					} else {
						throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
								"Server Error please try again later or contact us");
					}
				} else {
					throw new QuizException(quizItem, 0, QuizExceptionType.QuizStarted, "Quiz already started");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can start this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void stopQuiz(long quizId, long endTime, long quizManagerId)
			throws QuizServerException, InvalidInputException, NotExistsException, QuizException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizEndDate().getTime() <= System.currentTimeMillis()) {
				quizCopyItem = quizCopyRepository.findById(quizItem.getId()).orElse(null);
				if (quizCopyItem != null) {
					quizCopyRepository.deleteById(quizCopyItem.getId());
				}
			}
			if (quizItem.getQuizManagerId() == quizManagerId) {
				if (quizItem.getQuizStartDate().getTime() < endTime && quizItem.getQuizEndDate().getTime() > endTime) {
					quizItem.setQuizEndDate(new Date(endTime));
					if (quizRepository.existsById(quizItem.getId())) {
						quizRepository.save(quizItem);
					} else {
						throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
								"Server Error please try again later or contact us");
					}
				} else {
					throw new InvalidInputException(endTime, 0, "Invalid end time input");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can stop this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void addPlayerToPrivateQuiz(long quizId, long playerId, long quizManagerId)
			throws QuizServerException, NotExistsException, QuizException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizManagerId() == quizManagerId) {
				playerItem = playerRepository.findById(playerId).orElse(null);
				if (playerItem != null) {
					if (quizItem.getPlayers().add(playerItem)) {
						if (quizRepository.existsById(quizItem.getId())) {
							quizRepository.save(quizItem);
						} else {
							throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
									"Server Error please try again later or contact us");
						}
					} else {
						throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
								"Server Error please try again later or contact us");
					}
				} else {
					throw new NotExistsException(null, playerId, "Player with this id don't exists");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can add players to this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void updateAnswer(long quizId, long questionId, long answerId, long quizManagerId, String answerText)
			throws QuizServerException, NotExistsException, QuizException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizManagerId() == quizManagerId) {
				if (quizItem.getQuizStartDate() == null) {
					questionItem = questionRepository.findById(questionId).orElse(null);
					if (questionItem != null) {
						answerItem = answerRepository.findById(answerId).orElse(null);
						if (answerItem != null) {
							System.out.println("this is a check at main Quiz app in updateAnswer service method!!!");
							System.out.println(quizItem.getQuestions().stream()
									.map(q -> q.getAnswers().stream().filter(a -> a.getId() == answerId)));

							quizItem.getQuestions().get(quizItem.getQuestions().indexOf(questionItem)).getAnswers()
									.get(quizItem.getQuestions().get(quizItem.getQuestions().indexOf(questionItem))
											.getAnswers().indexOf(answerItem))
									.setAnswerText(answerText);
							if (quizRepository.existsById(quizItem.getId())) {
								quizRepository.save(quizItem);
							} else {
								throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
										"Server Error please try again later or contact us");
							}
							quizCopyItem = quizCopyRepository.findById(quizItem.getId()).orElse(null);
							// need to check if this works because Quiz question and quizcopy question are
							// diffrent!!!, if not working possibly need to set all question answers to
							// false!
							if (quizCopyItem != null) {
								quizCopyItem.getQuestions().get(quizCopyItem.getQuestions().indexOf(questionItem))
										.getAnswers()
										.get(quizCopyItem.getQuestions()
												.get(quizCopyItem.getQuestions().indexOf(questionItem)).getAnswers()
												.indexOf(answerItem))
										.setAnswerText(answerText);
								if (quizCopyRepository.existsById(quizCopyItem.getId())) {
									quizCopyRepository.save(quizCopyItem);
								} else {
									throw new QuizServerException(quizCopyItem, "QuizCopyRepository.save(quizCopyItem)",
											"Server Error please try again later or contact us");
								}
							} else {
								throw new QuizServerException(quizCopyItem,
										"QuizCopyRepository.findById(quizItem.getId()).orElse(null)",
										"Server Error please try again later or contact us");
							}
						} else {
							throw new NotExistsException(null, answerId, "Answer with this id don't exists");
						}
					} else {
						throw new NotExistsException(null, questionId, "Question with this id don't exists");
					}
				} else {
					throw new QuizException(quizItem, 0, QuizExceptionType.QuizStarted,
							"You can't update an on-going quiz");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can update answers of this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public ResponseEntity<?> updateQuestion(long quizId, long questionId, long quizManagerId, String questionText)
			throws QuizServerException, NotExistsException, QuizException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizManagerId() == quizManagerId) {
				if (quizItem.getQuizStartDate() == null) {
					questionItem = questionRepository.findById(questionId).orElse(null);
					if (questionItem != null) {
						quizItem.getQuestions().get(quizItem.getQuestions().indexOf(questionItem))
								.setQuestionText(questionText);
						if (quizRepository.existsById(quizItem.getId())) {
							quizRepository.save(quizItem);
						} else {

						}
						quizCopyItem = quizCopyRepository.findById(quizItem.getId()).orElse(null);
						if (quizCopyItem != null) {
							// need to check if this works because Quiz question and quizcopy question are
							// diffrent!!!, if not working possibly need to set all question answers to
							// false!
							quizCopyItem.getQuestions().get(quizCopyItem.getQuestions().indexOf(questionItem))
									.setQuestionText(questionText);
							if (quizCopyRepository.existsById(quizCopyItem.getId())) {
								quizCopyRepository.save(quizCopyItem);
							} else {
								throw new QuizServerException(quizCopyItem, "QuizCopyRepository.save(quizCopyItem)",
										"Server Error please try again later or contact us");
							}
						} else {
							throw new QuizServerException(quizCopyItem,
									"QuizCopyRepository.findById(quizItem.getId()).orElse(null)",
									"Server Error please try again later or contact us");
						}
					} else {
						throw new NotExistsException(null, questionId, "Question with this id don't exists");
					}
				} else {
					throw new QuizException(quizItem, 0, QuizExceptionType.QuizStarted,
							"You can't update an on-going quiz");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can update questions of this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void removeQuestion(long quizId, long questionId, @PathVariable long quizManagerId) {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizManagerId() == quizManagerId) {
				questionItem = questionRepository.findById(questionId).orElse(null);
				if (questionItem != null) {
					if (quizItem.getQuestions().remove(questionItem)) {
						if (quizRepository.existsById(quizId)) {
							quizRepository.save(quizItem);
						} else {

						}
						quizCopyItem = quizCopyRepository.findById(quizItem.getId()).orElse(null);
						if (quizCopyItem != null) {
							// need to check if it finds the Question object in the quizcopy because the
							// answers are not the same for quiz Question object and quizcopy Question
							// object
							if (quizCopyItem.getQuestions().remove(questionItem)) {
								if (quizCopyRepository.existsById(quizCopyItem.getId())) {
									quizCopyRepository.save(quizCopyItem);
								}
							}
						} else {

						}
					} else {
					}
				} else {
					throw new NotExistsException(null, questionItem, "Question with this id don't exists");
				}
			} else {
				throw new QuizException(quizItem, quizManagerId, QuizExceptionType.NotQuizManager,
						"Only this quiz manager can remove questions from this quiz");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
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
					quizInfo = new QuizInfo(0, quiz.getId(), quiz.getQuizName(), quiz.getWinnerPlayer().getId(),
							quiz.getWinnerPlayerScore(), playersMongo);
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
		quizItem = null;
		quizCopyItem = null;
		questionItem = null;
		answerItem = null;
		quizInfoItem = null;
		playersMongoItem = null;
	}

}
