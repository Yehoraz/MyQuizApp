package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.repos.QuestionRepository;

@Service
public class QuestionService {

	@Autowired
	private QuestionRepository repository;
	
	public void addQuestion(Question question) {
		//validation check here!!!!
		repository.save(question);
	}
	
	public void removeQuestion(Question question) {
		if(repository.existsById(question.getId())) {
			repository.delete(question);
		}else {
			throw new EntityNotFoundException("Question with id: " + question.getId() + " does not exists");
		}
	}
	
	public void updateQuestion(Question question) {
		if(repository.existsById(question.getId())) {
			repository.save(question);
		}else {
			throw new EntityNotFoundException("Question with id: " + question.getId() + " does not exists");
		}
	}
	
	public Question getQuestionById(long question_id) {
		if(repository.existsById(question_id)) {
			return repository.getOne(question_id);
		}else {
			throw new EntityNotFoundException("Question with id: " + question_id + " does not exists");
		}
	}
	
	public List<Question> getAllQuestions() throws EntityNotFoundException {
		if(repository.count() > 0) {
			return repository.findAll();
		}else {
			throw new EntityNotFoundException("Question database is empty");
		}
	}
	
}
