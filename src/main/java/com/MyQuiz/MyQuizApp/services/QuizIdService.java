package com.MyQuiz.MyQuizApp.services;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.QuizId;
import com.MyQuiz.MyQuizApp.repos.QuizIdRepository;

@Service
public class QuizIdService {

	@Autowired
	private QuizIdRepository repository;
	
	public void addQuizId(QuizId quizId) throws EntityExistsException{
		if(!repository.existsById(quizId.getQuizId())) {
			repository.save(quizId);
		}else {
			throw new EntityExistsException();
		}
	}
	
	public void removeQuizId(QuizId quizId) throws EntityNotFoundException{
		if(repository.existsById(quizId.getQuizId())) {
			repository.delete(quizId);
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	public boolean ifExistsById(long quiz_id) {
		return repository.existsById(quiz_id);
	}
	
}
