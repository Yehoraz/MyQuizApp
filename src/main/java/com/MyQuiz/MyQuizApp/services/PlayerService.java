package com.MyQuiz.MyQuizApp.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
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
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;
import com.MyQuiz.MyQuizApp.repos.QuestionRepository;
import com.MyQuiz.MyQuizApp.repos.QuizInfoRepository;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;
import com.MyQuiz.MyQuizApp.repos.SuggestedQuestionRepository;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@Service
public class PlayerService {

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private SuggestedQuestionRepository suggestedQuestionRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuizInfoRepository quizInfoRepository;

	private Quiz quizItem = null;
	private Player playerItem = null;
	private List<Question> questionsItem = null;
	private List<Question> randomQuestionsItem = null;
	private SuggestedQuestion sQuestionItem = null;
	private QuizInfo quizInfoItem = null;
	private int scoreItem = 0;
	private List<Quiz> quizsItem = null;

	private final int MAX_TIME_PAST_QUIZ_END = 1000 * 10;

	public void createQuiz(Quiz quiz) throws QuizServerException, QuizException, InvalidInputException {
		restartVariables();
		quiz.setId(createQuizId());
		if (ValidationUtil.validationCheck(quiz)) {
			if (!quizRepository.existsByQuizManagerIdAndQuizEndDateAfter(quiz.getQuizManagerId(),
					new Date(System.currentTimeMillis()))) {
				for (int i = 0; i < quiz.getQuestions().size(); i++) {
					quiz.getQuestions().get(i).setApproved(false);
				}
				if (!quizRepository.existsById(quiz.getId())) {
					quizRepository.save(quiz);
				} else {
					throw new QuizServerException(quiz, "QuizRepository.save(quiz)",
							"Server Error please try again later or contact us");
				}
			} else {
				throw new QuizException(quiz, quiz.getId(), QuizExceptionType.AlreadyManageQuiz,
						"Player already manage another quiz");
			}
		} else {
			throw new InvalidInputException(quiz, quiz.getId(), "Invalid quiz input");
		}
	}

	// urgent!!! need to check if everything (stream and lambda) works good!!! test
	// for exceptions!!!
	public int answerQuiz(long quizId, QuizPlayerAnswers playerAnswers)
			throws QuizServerException, QuizException, NotExistsException, InvalidInputException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (ValidationUtil.validationCheck(playerAnswers)) {
				playerItem = playerRepository.findById(playerAnswers.getPlayerId()).orElse(null);
				if (playerItem != null) {
					playerAnswers.setCompletionTime(
							playerAnswers.getCompletionTime() - quizItem.getQuizStartDate().getTime());
					if (quizItem.getPlayers().contains(playerItem)) {
						if (quizItem.getQuizEndDate() != null && quizItem.getQuizEndDate()
								.getTime() > (System.currentTimeMillis() - MAX_TIME_PAST_QUIZ_END)) {
							if (quizItem.getQuizPlayerAnswers().stream()
									.filter(p -> p.getPlayerId() == playerAnswers.getPlayerId()).count() < 1) {
								scoreItem = (int) quizItem.getQuestions().stream().filter(
										q -> q.getCorrectAnswerId() == playerAnswers.getPlayerAnswers().get(q.getId()))
										.count();
								playerAnswers.setScore(scoreItem);
								quizItem.getQuizPlayerAnswers().add(playerAnswers);
								if (quizItem.getWinnerPlayerScore() < scoreItem) {
									quizItem.setWinnerPlayer(playerItem);
									quizItem.setWinnerPlayerScore(scoreItem);
								}
								if (quizRepository.existsById(quizItem.getId())) {
									quizRepository.save(quizItem);
									return scoreItem;
								} else {
									throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
											"Server Error please try again later or contact us");
								}
							} else {
								throw new QuizException(playerItem, 0, QuizExceptionType.AlreadyAnswered,
										"Player has already answered this quiz");
							}
						} else {
							throw new QuizException(quizItem, 0, QuizExceptionType.QuizEnded,
									"Time to answer the Quiz has passed");
						}
					} else {
						throw new QuizException(playerItem, 0, QuizExceptionType.NotQuizPlayer,
								"Player don't belong to this quiz");
					}
				} else {
					throw new NotExistsException(playerAnswers, 0, "Player with this id don't exists");
				}
			} else {
				throw new InvalidInputException(playerAnswers, 0, "Invalid player answers input");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void joinQuiz(long quizId, long playerId) throws QuizServerException, NotExistsException, QuizException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.isQuizPrivate() == false) {
				if (quizItem.getQuizEndDate() == null
						|| quizItem.getQuizEndDate().getTime() < System.currentTimeMillis()) {
					playerItem = playerRepository.findById(playerId).orElse(null);
					if (playerItem != null) {
						quizItem.getPlayers().add(playerItem);
						if (quizRepository.existsById(quizItem.getId())) {
							quizRepository.save(quizItem);
						} else {
							throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
									"Server Error please try again later or contact us");
						}
					} else {
						throw new NotExistsException(null, playerId, "Player with this id don't exists");
					}
				} else {
					throw new QuizException(quizItem, 0, QuizExceptionType.QuizEnded, "Quiz has ended");
				}
			} else {
				throw new QuizException(quizItem, 0, QuizExceptionType.QuizIsPrivate, "Quiz is private");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void leaveQuiz(long quizId, long playerId) throws QuizServerException, QuizException, NotExistsException {
		restartVariables();
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			playerItem = playerRepository.findById(playerId).orElse(null);
			if (playerItem != null) {
				if (quizItem.getQuizStartDate() == null) {
					if (quizItem.getPlayers().remove(playerItem)) {
						if (quizRepository.existsById(quizItem.getId())) {
							quizRepository.save(quizItem);
						} else {
							throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
									"Server Error please try again later or contact us");
						}
					} else {
						throw new QuizException(playerItem, 0, QuizExceptionType.NotQuizPlayer,
								"Player don't belong to this quiz");
					}
				} else {
					if ((quizItem.getQuizPlayerAnswers().stream().filter(qp -> qp.getPlayerId() == playerItem.getId())
							.count() < 1)) {
						quizItem.getQuizPlayerAnswers()
								.add(new QuizPlayerAnswers(playerItem.getId(), 0, 99999999999999999l, null));
					}
					quizItem.getPlayers().remove(playerItem);
					if (quizRepository.existsById(quizItem.getId())) {
						quizRepository.save(quizItem);
					} else {
						throw new QuizServerException(quizItem, "QuizRepository.save(quizItem)",
								"Server Error please try again later or contact us");
					}
				}
			} else {
				throw new NotExistsException(null, playerId, "Player with this id don't exists");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void suggestQuestion(long playerId, Question question) throws InvalidInputException, ExistsException {
		if (ValidationUtil.validationCheck(question)) {
			if (!questionRepository.existsByQuestionText(question.getQuestionText())) {
				suggestedQuestionRepository.save(new SuggestedQuestion(0, playerId, question));
			} else {
				throw new ExistsException(question, 0, "Question with this text already exists");
			}
		} else {
			throw new InvalidInputException(question, 0, "Invalid question input");
		}
	}

	public void updateSuggestedQuestion(long playerId, SuggestedQuestion suggestedQuestion)
			throws QuizServerException, QuizException, InvalidInputException, NotExistsException {
		restartVariables();
		sQuestionItem = suggestedQuestionRepository.findById(suggestedQuestion.getId()).orElse(null);
		if (sQuestionItem != null) {
			if (ValidationUtil.validationCheck(suggestedQuestion)) {
				if (sQuestionItem.getPlayerId() == playerId) {
					if (suggestedQuestionRepository.existsById(suggestedQuestion.getId())) {
						suggestedQuestionRepository.save(suggestedQuestion);
					} else {
						throw new QuizServerException(quizItem, "SuggestedQuestionRepository.save(suggestedQuestion)",
								"Server Error please try again later or contact us");
					}
				} else {
					throw new QuizException(suggestedQuestion, playerId, QuizExceptionType.NotSuggestedPlayer,
							"Only the suggested player can updated this suggested question");
				}
			} else {
				throw new InvalidInputException(suggestedQuestion, 0, "Invalid suggested question input");
			}
		} else {
			throw new NotExistsException(null, suggestedQuestion.getId(),
					"Suggested Question with this id don't exists");
		}
	}

	public List<Question> getAllQuestions() throws NotExistsException {
		restartVariables();
		questionsItem = questionRepository.findByIsApproved(true);
		if (questionsItem != null) {
			return questionsItem;
		} else {
			throw new NotExistsException(null, 0, "There are no approved questions");
		}
	}

	public List<Question> getRandomQuestions(short numberOfRandomQuestions) throws NotExistsException {
		restartVariables();
		questionsItem = questionRepository.findByIsApproved(true);
		if (questionsItem != null) {
			if (questionsItem.size() <= numberOfRandomQuestions) {
				return questionsItem;
			} else {
				randomQuestionsItem = new ArrayList<Question>();
				while (randomQuestionsItem.size() < numberOfRandomQuestions) {
					randomQuestionsItem
							.add(questionsItem.get(Math.max(1, ((int) Math.random() * questionsItem.size()))));
				}
				return randomQuestionsItem;
			}
		} else {
			throw new NotExistsException(null, 0, "There are no approved questions");
		}
	}

	public QuizInfo getQuizInfo(long quizId) throws NotExistsException {
		restartVariables();
		quizInfoItem = quizInfoRepository.findById(quizId).orElse(null);
		if (quizInfoItem != null) {
			return quizInfoItem;
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public List<Quiz> getAllPrevQuizs(long playerId) throws NotExistsException {
		quizsItem = quizRepository.findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateBefore(playerId,
				new Date(System.currentTimeMillis()));
		if (quizsItem != null) {
			return quizsItem;
		} else {
			throw new NotExistsException(null, playerId, "You never managed any quiz");
		}
	}

	private void restartVariables() {
		quizItem = null;
		playerItem = null;
		questionsItem = null;
		randomQuestionsItem = null;
		sQuestionItem = null;
		quizInfoItem = null;
		scoreItem = 0;
		quizsItem = null;
	}

	private long createQuizId() {
		long quizId;
		do {
			quizId = (long) Math.abs((Math.random() * 1000000000000000000l));
		} while (quizRepository.existsById(quizId));
		return quizId;
	}

}
