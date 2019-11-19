package com.MyQuiz.MyQuizApp.services;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.repos.SuggestedQuestionRepository;

@Service
public class SuggestedQuestionService {

	@Autowired
	private SuggestedQuestionRepository repository;

	public void addSuggestedQuestion(SuggestedQuestion suggestedQuestion) throws EntityExistsException {
		if (repository.findByQuestion(suggestedQuestion.getQuestion())) {
			repository.save(suggestedQuestion);
		} else {
			throw new EntityExistsException();
		}
	}

	public void updateSuggestedQuestion(SuggestedQuestion suggestedQuestion) {
		if (repository.existsById(suggestedQuestion.getId())) {
			repository.save(suggestedQuestion);
		} else {
			throw new EntityNotFoundException();
		}
	}

	public SuggestedQuestion getSuggestedQuestion(long sQuestionId) {
		return repository.findById(sQuestionId).orElse(null);
	}

}
