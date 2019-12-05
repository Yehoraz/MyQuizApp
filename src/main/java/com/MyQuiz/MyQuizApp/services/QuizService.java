package com.MyQuiz.MyQuizApp.services;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.repos.QuizRepository;

@Service
public class QuizService {

	@Autowired
	private QuizRepository repository;

	public Quiz addQuiz(Quiz quiz) throws EntityExistsException {
		if (!repository.existsById(quiz.getId())) {
			return repository.save(quiz);
		} else {
			return null;
		}
	}

	public void removeQuiz(Quiz quiz) throws EntityNotFoundException {
		if (repository.existsById(quiz.getId())) {
			repository.delete(quiz);
		} else {
			throw new EntityNotFoundException("Quiz with id: " + quiz.getId() + " does not exists");
		}
	}

	public Quiz updateQuiz(Quiz quiz) throws EntityNotFoundException {
		if (repository.existsById(quiz.getId())) {
			return repository.save(quiz);
		} else {
			return null;
		}
	}

	public Quiz getQuizById(long quizId) {
		return repository.findById(quizId).orElse(null);
	}
	
	public Quiz getQuizOpenByManagerId(long quizManagerId) {
		return repository.findByQuizManagerIdAndQuizStartDateIsNull(quizManagerId).orElse(null);
	}
	
	public Quiz getQuizStartByManagerId(long quizManagerId) {
		return repository.findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateAfter(quizManagerId, new Date(System.currentTimeMillis())).orElse(null);
	}
	
	public List<Quiz> getAllPrevQuizs(long quizManagerId){
		return repository.findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateBefore(quizManagerId, new Date(System.currentTimeMillis()));
	}

	public List<Quiz> getAllQuizs() {
		if (repository.count() > 0) {
			return repository.findAll();
		} else {
			return null;
		}
	}

	public void deleteExpiredQuizs(Date date) {
		if (repository.count() > 0) {
			repository.deleteByQuizEndDateBefore(date);
		}
	}
	
	public boolean ifPlayerHasQuizOpen(long playerId) {
		return repository.existsByQuizManagerIdAndQuizEndDateAfter(playerId, new Date(System.currentTimeMillis()));
	}

	public boolean ifExistsById(long quizId) {
		return repository.existsById(quizId);
	}

}
