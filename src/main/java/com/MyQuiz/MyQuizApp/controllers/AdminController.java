package com.MyQuiz.MyQuizApp.controllers;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuestionService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.services.SuggestedQuestionService;

@RestController
@Lazy
public class AdminController {
	
	@Autowired
	private QuizService quizService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private SuggestedQuestionService suggestedQuestionService;
	
	
	@PostMapping("/addQuestion")
	public ResponseEntity<?> addQuestion(@RequestBody Question question) {
		questionService.addQuestion(question);
		return ResponseEntity.status(HttpStatus.OK).body("Question added");
	}
	
	@GetMapping("/getSuggestedQuestions")
	public ResponseEntity<?> getAllSuggestedQuestions(){
		List<SuggestedQuestion> suggestedQuestions = null;
		try {
			suggestedQuestions = (List<SuggestedQuestion>)Hibernate.unproxy(suggestedQuestionService.getAllSuggestedQuestions());
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thre are no suggested questions");
		}
		return ResponseEntity.status(HttpStatus.OK).body(suggestedQuestions);
	}
	
	@PostMapping("/addSuggestedQuestion")
	public ResponseEntity<?> addSuggestedQuestion(SuggestedQuestion suggestedQuestion){
		questionService.addQuestion(suggestedQuestion.getQuestion());
		return ResponseEntity.status(HttpStatus.OK).body("Question added");
	}
	
	@PostMapping("/addAllSuggestedQuestions")
	public ResponseEntity<?> addAllSuggestedQuestions(){
		List<SuggestedQuestion> suggestedQuestions = null;
		try {
			suggestedQuestions = suggestedQuestionService.getAllSuggestedQuestions();
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no suggested questions");
		}
		
		for(SuggestedQuestion sq:suggestedQuestions) {
			questionService.addQuestion(sq.getQuestion());
		}
		if (deleteAllSuggestedQuestions().getStatusCodeValue() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body("All suggested questions added");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some suggested questions might have a problem");
		}
	}
	
	@DeleteMapping("/deleteSuggestedQuestion/{sqId}")
	public ResponseEntity<?> deleteSuggestedQuestion(@PathVariable("sqID") long suggestedQuestionID){
		try {
			SuggestedQuestion sQuestion = suggestedQuestionService.getSuggestedQuestion(suggestedQuestionID);
			suggestedQuestionService.removeSuggestedQuestion(sQuestion);
			return ResponseEntity.status(HttpStatus.OK).body("Suggested question removed");
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggested question does not exists");
		}
	}
	
	@DeleteMapping("/deleteAllSuggestedQuestions")
	public ResponseEntity<?> deleteAllSuggestedQuestions(){
		try {
			suggestedQuestionService.removeAllSuggestedQuestions();
			return ResponseEntity.status(HttpStatus.OK).body("All suggested questions has been removed");
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no suggested questions");
		}
	}
	
	
	@DeleteMapping("/deleteExpiredQuizs")
	public void deleteExpiredQuizs() {
			long deleteTimeStamp = (System.currentTimeMillis() - ((1000 * 60 * 60 * 24) * 30));
			try {
				quizService.deleteExpiredQuizs(new Date(deleteTimeStamp));
			} catch (EntityNotFoundException e) {
				System.out.println("some logger");
			}
	}
	
	@PostMapping("/addPlayer")
	public ResponseEntity<?> addPlayer(@RequestBody Player player){
		try {
			playerService.addPlayer(player);
			return ResponseEntity.status(HttpStatus.OK).body("Player added");
		}catch (EntityExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player already exists");
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player info is invalid");
		}
	}
	
	
	//both are check methods!
	@GetMapping("/getQuizFromMe/{id}")
	public Quiz getQuizFromMe(@PathVariable long id) {
		return (Quiz)Hibernate.unproxy(quizService.getQuizById(id));
	}
	
	@GetMapping("/checkQuiz/{quiz_id}")
	public void getQuizYay(@PathVariable long quiz_id) {
		Quiz quiz = quizService.getQuizById(quiz_id);
	}
	
}
