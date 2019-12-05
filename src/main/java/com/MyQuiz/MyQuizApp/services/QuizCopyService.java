package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.repos.QuizCopyRepository;

@Service
public class QuizCopyService {

	@Autowired
	private QuizCopyRepository repository;

	public void addQuizCopy(QuizCopy quizCopy) throws EntityExistsException {
		if (!repository.existsById(quizCopy.getId())) {
			repository.save(quizCopy);
		} else {
			throw new EntityExistsException();
		}
	}

	public void removeQuizCopy(QuizCopy quizCopy) throws EntityNotFoundException {
		if (repository.existsById(quizCopy.getId())) {
			repository.delete(quizCopy);
		} else {
			throw new EntityNotFoundException();
		}
	}

	public void updateQuizCopy(QuizCopy quizCopy) throws EntityNotFoundException {
		if (repository.existsById(quizCopy.getId())) {
			repository.save(quizCopy);
		} else {
			throw new EntityNotFoundException();
		}
	}

	public QuizCopy getQuizCopy(long quizId) {
			return repository.findById(quizId).orElse(null);
	}
	
	public List<QuizCopy> getAllQuizCopies(){
		return repository.findAll();
	}

}
