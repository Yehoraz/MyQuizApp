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
	
	public void addQuestion(Question question) throws EntityExistsException {
		if(!repository.existsByQuestionText(question.getQuestionText())) {
			repository.save(question);
		}else {
			throw new EntityExistsException();
		}
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
	
	public Question getQuestionById(long questionId) {
			return repository.findById(questionId).orElse(null);
	}
	
	public List<Question> getAllQuestions() throws EntityNotFoundException {
			return repository.findAll();
	}
	
	public List<Question> getAllApprovedQuestions(){
		return repository.findByIsApproved(true);
	}
	
}
