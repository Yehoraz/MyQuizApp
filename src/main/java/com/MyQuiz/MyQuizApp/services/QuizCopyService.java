package com.MyQuiz.MyQuizApp.services;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.repos.QuizCopyRepository;

@Service
public class QuizCopyService {

	@Autowired
	private QuizCopyRepository repository;
	
	
	public void addQuizCopy(QuizCopy quizCopy) throws EntityExistsException, InvalidInputException{
		if(validateItemInfo(quizCopy)) {
		if(!repository.existsById(quizCopy.getId())) {
			repository.save(quizCopy);
		}else {
			throw new EntityExistsException();
		}}else {
			throw new InvalidInputException(quizCopy.getId(), quizCopy.getQuizName(), "validateItemInfo() returned false", "Quiz info is invalid!");

		}
	}
	
	public void removeQuizCopy(QuizCopy quizCopy) throws EntityNotFoundException, InvalidInputException{
		if(validateItemInfo(quizCopy)) {
		if(repository.existsById(quizCopy.getId())) {
			repository.delete(quizCopy);
		}else {
			throw new EntityNotFoundException();
		}
		}
		else {
			throw new InvalidInputException(quizCopy.getId(), quizCopy.getQuizName(), "validateItemInfo() returned false", "Quiz info is invalid!");
		}
	}
	
	public void updateQuizCopy(QuizCopy quizCopy) throws EntityNotFoundException, InvalidInputException{
		if(validateItemInfo(quizCopy)) {
		if(repository.existsById(quizCopy.getId())) {
			repository.save(quizCopy);
		}else {
			throw new EntityNotFoundException();
		}
		}else {
			throw new InvalidInputException(quizCopy.getId(), quizCopy.getQuizName(), "validateItemInfo() returned false", "Quiz info is invalid!");
		}
	}
	
	public QuizCopy getQuizCopy(long quiz_id) throws EntityNotFoundException{
		if(repository.existsById(quiz_id)) {
			return repository.getOne(quiz_id);
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	private boolean validateItemInfo(Object obj) {
		if (obj instanceof QuizCopy) {
			QuizCopy tempQuiz = (QuizCopy) obj;
			if (tempQuiz.getId() > 100000000000000000l
					|| tempQuiz.getQuestions() == null
					|| tempQuiz.getQuestions().size() < 1 || tempQuiz.getQuizName().length() < 2) {
				return false;
			}
			for (Question q : tempQuiz.getQuestions()) {
				if (q.getAnswers() == null || q.getAnswers().size() < 2) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
}
