package com.MyQuiz.MyQuizApp.utils;

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswers;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;

public class ValidationUtil {

	public static boolean validationCheck(Object obj) {
		if (obj != null) {
			if (obj instanceof Quiz) {
				Quiz tempQuiz = (Quiz) obj;
				if (tempQuiz.getQuizName().length() < 1 || tempQuiz.getId() < 100000000000000000l || tempQuiz.getPlayers() == null || tempQuiz.getQuestions() == null ||
						tempQuiz.getQuestions().size() < 1 || tempQuiz.getQuizOpenDate() == null
						|| tempQuiz.getWinnerPlayer() != null ||
						 tempQuiz.getQuizManagerId() == 0
						|| tempQuiz.getQuizMaxTimeInMillis() < (1000 * 10)) {
					return false;
				} else {
					if (tempQuiz.getQuestions().stream().filter(q -> (!validationCheck(q))).count() > 0) {
						return false;
					} else {
						return true;
					}
				}
			} else if (obj instanceof SuggestedQuestion) {
				SuggestedQuestion sQuestion = (SuggestedQuestion) obj;
				if (sQuestion.getId() < 0 || sQuestion.getPlayerId() < 1 || sQuestion.getQuestion() == null) {
					return false;
				} else {
					return validationCheck(sQuestion.getQuestion());
				}
			} else if (obj instanceof Question) {
				Question question = (Question) obj;
				if (question.getCorrectAnswerId() < 0 || question.getId() < 0 || question.getQuestionText().length() < 1
						|| question.getAnswers() == null) {
					return false;
				} else {
					if (question.getAnswers().stream().filter(a -> (!validationCheck(a))).count() > 0) {
						return false;
					} else {
						return true;
					}
				}
			} else if (obj instanceof Answer) {
				Answer answer = (Answer) obj;
				if (answer.getId() < 0 || answer.getAnswerText().length() < 1) {
					return false;
				} else {
					return true;
				}
			} else if (obj instanceof Player) {
				Player tempPlayer = (Player) obj;
				if (tempPlayer.getId() < 1 || tempPlayer.getAge() < 1 || tempPlayer.getFirstName().length() < 1
						|| tempPlayer.getLastName().length() < 1) {
					return false;
				} else {
					return true;
				}
			} else if (obj instanceof QuizPlayerAnswers) {
				QuizPlayerAnswers qPlayerAnswers = (QuizPlayerAnswers) obj;
				if (qPlayerAnswers.getScore() < 0 || qPlayerAnswers.getPlayerId() < 0
						|| qPlayerAnswers.getCompletionTime() < 0 || qPlayerAnswers.getPlayerAnswers() == null) {
					return false;
				} else {
					if (qPlayerAnswers.getPlayerAnswers().values().stream().filter(v -> v < 0).count() > 0) {
						return false;
					} else {
						return true;
					}
				}
			} else if (obj instanceof QuizCopy) {
				QuizCopy tempQuizCopy = (QuizCopy) obj;
				if (tempQuizCopy.getId() > 100000000000000000l || tempQuizCopy.getQuestions() == null
						|| tempQuizCopy.getQuestions().size() < 1 || tempQuizCopy.getQuizName().length() < 1) {
					return false;
				}
				if (tempQuizCopy.getQuestions().stream().filter(q -> (!validationCheck(q))).count() > 0) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
