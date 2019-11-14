package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.repos.AnswerRepository;

@Service
public class AnswerService {

	
	//need to make exceptions for everything and add the logic!!!!
	
	@Autowired
	private AnswerRepository repository;
	
	public void addAnswer(Answer answer) {
		if(!repository.existsById(answer.getId())) {
			repository.save(answer);
		}else {
			throw new EntityExistsException("Answer with id: " + answer.getId() + " already exists");
		}
	}
	
	public void removeAnswer(Answer answer) {
		if(repository.existsById(answer.getId())) {
			repository.delete(answer);
		}else {
			throw new EntityNotFoundException("Answer with id: " + answer.getId() + " does not exists");
		}
	}
	
	public void updateAnswer(Answer answer) {
		if(repository.existsById(answer.getId())) {
			repository.save(answer);
		}else {
			throw new EntityNotFoundException("Answer with id: " + answer.getId() + " does not exists");
		}
	}
	
	public Answer getAnswerById(long answer_id) {
		if(repository.existsById(answer_id)) {
			return repository.getOne(answer_id);
		}else {
			throw new EntityNotFoundException("Answer with id: " + answer_id + " does not exists");
		}
	}
	
	public List<Answer> getAllAnswers(){
		if(repository.count() > 0) {
			return repository.findAll();
		}else {
			throw new EntityNotFoundException("Answer database is empty");
		}
	}
	
	
}
