package com.MyQuiz.MyQuizApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.exceptions.ExistsException;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.exceptions.NotExistsException;
import com.MyQuiz.MyQuizApp.exceptions.QuizException;
import com.MyQuiz.MyQuizApp.services.GeneralService;

@RestController
public class GeneralController {

	@Autowired
	private GeneralService generalService;

	@PostMapping("/addPlayer")
	public ResponseEntity<?> addPlayer(@RequestBody Player player) {
		try {
			generalService.addPlayer(player);
		} catch (ExistsException e1) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Player id already exists");
		} catch (InvalidInputException e1) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Player added");
	}

	@PutMapping("/updatePlayer")
	public ResponseEntity<?> updatePlayer(@RequestBody Player player) {
		try {
			generalService.updatePlayer(player);
		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping("/deleteExpiredQuizs")
	public void deleteExpiredQuizs() {
		generalService.deleteExpiredQuizs();
	}

	@GetMapping("/getWinner/{quizId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quizId) {
		Quiz quiz = null;
		try {
			quiz = generalService.getEndedQuiz(quizId);

		} catch (NotExistsException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		} catch (QuizException e) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz not finished yet");
		}
		return ResponseEntity.status(HttpStatus.OK).body(quiz.getWinnerPlayer().getFirstName() + " "
				+ quiz.getWinnerPlayer().getLastName() + " won with score of: " + quiz.getWinnerPlayerScore());
	}

}
