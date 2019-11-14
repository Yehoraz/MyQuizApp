package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.repos.SuggestedQuestionRepository;

@Service
public class SuggestedQuestionService {

	@Autowired
	private SuggestedQuestionRepository repository;
	
	public void addSuggestedQuestion(SuggestedQuestion suggestedQuestion) throws InvalidInputException {
		if(validationCheck(suggestedQuestion)) {
			repository.save(suggestedQuestion);
		}else {
			//need to complete the text!!!
			throw new InvalidInputException(suggestedQuestion.getId(), ""+suggestedQuestion.getPlayerId(), "", "");
		}
	}
	
	public void removeSuggestedQuestion(SuggestedQuestion suggestedQuestion) {
		if(repository.existsById(suggestedQuestion.getId())) {
			repository.delete(suggestedQuestion);
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	public void updateSuggestedQuestion(SuggestedQuestion suggestedQuestion) {
		if(repository.existsById(suggestedQuestion.getId())) {
			//validation check
			repository.save(suggestedQuestion);
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	public SuggestedQuestion getSuggestedQuestion(long question_id) {
		if(repository.existsById(question_id)) {
			return repository.getOne(question_id);
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	public List<SuggestedQuestion> getAllSuggestedQuestions(){
		if(repository.count() > 0) {
			return repository.findAll();
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	public void removeAllSuggestedQuestions() {
		if(repository.count() > 0) {
			repository.deleteAll();
		}else {
			throw new EntityNotFoundException();
		}
	}
	
	private boolean validationCheck(Object obj) {
		return true;
	}
	
}
