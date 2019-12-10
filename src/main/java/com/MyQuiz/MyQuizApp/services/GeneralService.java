package com.MyQuiz.MyQuizApp.services;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.enums.QuizExceptionType;
import com.MyQuiz.MyQuizApp.exceptions.ExistsException;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.NotExistsException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@Service
public class GeneralService {

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private PlayerRepository playerRepository;

	private Quiz quizItem = null;
	long deleteTimeStampItem = 0;

	private final int MAX_TIME_PAST_QUIZ_END = 1000 * 10;

	public void addPlayer(Player player) throws ExistsException, InvalidInputException {
		if (ValidationUtil.validationCheck(player)) {
			if (!playerRepository.existsById(player.getId())) {
				playerRepository.save(player);
			} else {
				throw new ExistsException(player, 0, "Player with this id already exists");
			}
		} else {
			throw new InvalidInputException(player, 0, "Invalid player input");
		}
	}

	public void updatePlayer(Player player) throws NotExistsException, InvalidInputException {
		if (ValidationUtil.validationCheck(player)) {
			if (playerRepository.existsById(player.getId())) {
				playerRepository.save(player);
			} else {
				throw new NotExistsException(player, 0, "Player with this id don't exists");
			}
		} else {
			throw new InvalidInputException(player, 0, "Invalid player input");
		}
	}

	public Quiz getEndedQuiz(long quizId) throws NotExistsException, QuizException {
		quizItem = null;
		quizItem = quizRepository.findById(quizId).orElse(null);
		if (quizItem != null) {
			if (quizItem.getQuizEndDate() != null
					&& ((System.currentTimeMillis() - quizItem.getQuizEndDate().getTime()) > MAX_TIME_PAST_QUIZ_END)) {
				return quizItem;
			} else {
				throw new QuizException(quizItem, 0, QuizExceptionType.QuizNotEnded, "Quiz not ended yet");
			}
		} else {
			throw new NotExistsException(null, quizId, "Quiz with this id don't exists");
		}
	}

	public void deleteExpiredQuizs() {
		deleteTimeStampItem = 0;
		deleteTimeStampItem = (System.currentTimeMillis() - ((1000 * 60 * 60 * 24) * 30));
		quizRepository.deleteByQuizEndDateBefore(new Date(deleteTimeStampItem));
	}

}
