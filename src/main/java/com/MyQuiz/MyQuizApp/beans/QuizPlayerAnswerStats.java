package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class QuizPlayerAnswerStats {

	@Id
	private long playerId;
	
	private String quizName;

	// generate by Server Side
	private int score;

	// generate by Server Side
	private long completionTime;


}
