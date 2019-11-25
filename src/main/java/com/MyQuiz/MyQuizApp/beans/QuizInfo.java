package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class QuizInfo {
	
	@Id
	private long quizId;
	
	private String quizName;
	
	private long winnerPlayerId;
	
	@OneToMany
	private List<PlayerMongo> quizPlayers;
	
}
