package com.MyQuiz.MyQuizApp.controllers;

import java.sql.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

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
import com.MyQuiz.MyQuizApp.services.PlayerService;
import com.MyQuiz.MyQuizApp.services.QuizService;
import com.MyQuiz.MyQuizApp.utils.ValidationUtil;

@RestController
public class GeneralController {

	@Autowired
	private QuizService quizService;

	@Autowired
	private PlayerService playerService;

	private Quiz quiz = null;
	private Player player = null;
	private final int MAX_TIME_PAST_QUIZ_END = 1000 * 10;

	@GetMapping("/getWinner/{quizId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quizId) {
		quiz = quizService.getQuizById(quizId);
		if (quiz != null) {
			if (quiz.getQuizEndDate() != null
					&& ((System.currentTimeMillis() - quiz.getQuizEndDate().getTime()) > MAX_TIME_PAST_QUIZ_END)) {
				return ResponseEntity.status(HttpStatus.OK).body(quiz.getWinnerPlayer().getFirstName() + " "
						+ quiz.getWinnerPlayer().getLastName() + " won with score of: " + quiz.getWinnerPlayerScore());
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz not finished yet");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz does not exists");
		}
	}

	@PutMapping("/updatePlayerInfo")
	public ResponseEntity<?> updatePlayerInfo(@RequestBody Player newPlayerInfo) {
		if (ValidationUtil.validationCheck(newPlayerInfo)) {
			player = playerService.getPlayerById(newPlayerInfo.getId());
			if (player != null) {
				try {
					playerService.updatePlayer(newPlayerInfo);
				} catch (EntityNotFoundException e) {
					return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
				}
				return ResponseEntity.status(HttpStatus.OK).body("Player updated");
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Player does not exists");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
		}
	}

	@PostMapping("/addPlayer")
	public ResponseEntity<?> addPlayer(@RequestBody Player player) {
		if (ValidationUtil.validationCheck(player)) {
			try {
				playerService.addPlayer(player);
				return ResponseEntity.status(HttpStatus.OK).body("Player added");
			} catch (EntityExistsException e) {
				return ResponseEntity.status(HttpStatus.CREATED).body("Player id already exists");
			}
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Invalid input");
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

}
