package com.MyQuiz.MyQuizApp.services;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;

@Service
public class QuizService {

	@Autowired
	private QuizRepository repository;

	public void addQuiz(Quiz quiz) throws EntityExistsException, InvalidInputException {
		if (validateItemInfo(quiz)) {
			if (repository.existsById(quiz.getId())) {
				throw new EntityExistsException("Quiz with id: " + quiz.getId() + " already exists");
			} else {
				repository.save(quiz);
			}
		} else {
			throw new InvalidInputException(quiz.getId(), quiz.getQuizName(), "validateItemInfo() returned false",
					"Quiz info is invalid!");
		}
	}

	public void removeQuiz(Quiz quiz) throws InvalidInputException {
		if (validateItemInfo(quiz)) {
			if (repository.existsById(quiz.getId())) {
				repository.delete(quiz);
			} else {
				throw new EntityNotFoundException("Quiz with id: " + quiz.getId() + " does not exists");
			}
		} else {
			throw new InvalidInputException(quiz.getId(), quiz.getQuizName(), "validateItemInfo() returned false",
					"Quiz info is invalid!");
		}
	}

	public void updateQuiz(Quiz quiz) throws InvalidInputException {
		if (validateItemInfo(quiz)) {
			if (repository.existsById(quiz.getId())) {
				repository.save(quiz);
			} else {
				throw new EntityNotFoundException("Quiz with id: " + quiz.getId() + " does not exists");
			}
		} else {
			throw new InvalidInputException(quiz.getId(), quiz.getQuizName(), "validateItemInfo() returned false",
					"Quiz info is invalid!");
		}
	}

	public void updateQuizWinnerPlayer(long quiz_id, Player player, int currentPlayerScore)
			throws InvalidInputException {
		if (validateItemInfo(player)) {
			if (repository.existsById(quiz_id)) {
				Quiz quiz = repository.getOne(quiz_id);
				if (quiz.getWinnerPlayerScore() < currentPlayerScore) {
					quiz.setWinnerPlayer(player);
					quiz.setWinnerPlayerScore(currentPlayerScore);
					repository.save(quiz);
				}
			} else {
				throw new EntityNotFoundException("Quiz with id: " + quiz_id + " does not exists");
			}
		} else {
			throw new InvalidInputException(player.getId(), player.getFirstName() + " " + player.getLastName(),
					"validateItemInfo() returned false", "Player info is invalid!");
		}
	}

	public Quiz getQuizById(long quiz_id) throws EntityNotFoundException {
		if (repository.existsById(quiz_id)) {
			return repository.getOne(quiz_id);
		} else {
			throw new EntityNotFoundException("Quiz with id: " + quiz_id + " does not exists");
		}
	}

	public List<Quiz> getAllQuizs() {
		if (repository.count() > 0) {
			return repository.findAll();
		} else {
			throw new EntityNotFoundException("Quiz database is empty");
		}
	}

	public void deleteExpiredQuizs(Date date) {
		if (repository.count() > 0) {
			repository.deleteByQuizEndDateBefore(date);
		} else {
			throw new EntityNotFoundException("Quiz database is empty");
		}
	}

	private boolean validateItemInfo(Object obj) {
		if (obj instanceof Quiz) {
			Quiz tempQuiz = (Quiz) obj;
			if (tempQuiz.getPlayers() == null || tempQuiz.getPlayers().size() < 0 || tempQuiz.getQuestions() == null
					|| tempQuiz.getQuestions().size() < 1 || tempQuiz.getQuizEndDate() != null
					|| tempQuiz.getQuizStartDate() != null || tempQuiz.getWinnerPlayer() != null
					|| tempQuiz.getWinnerPlayerScore() > 0 || tempQuiz.getQuizManager() == null
					|| tempQuiz.getQuizMaxTimeInMillis() < (1000 * 10) || tempQuiz.getQuizName().length() < 2
					|| tempQuiz.getQuizPlayerAnswers() != null) {
				return false;
			}
			for (Question q : tempQuiz.getQuestions()) {
				if (q.getAnswers() == null || q.getAnswers().size() < 2) {
					return false;
				}
			}
			return true;
		} else if (obj instanceof Player) {
			Player tempPlayer = (Player) obj;
			if (tempPlayer.getId() < 1 || tempPlayer.getAge() < 1 || tempPlayer.getFirstName().length() < 2
					|| tempPlayer.getLastName().length() < 2) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
