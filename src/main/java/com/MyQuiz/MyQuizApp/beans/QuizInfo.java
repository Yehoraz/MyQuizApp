package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuizInfo {
	
	@Id
	private long quizId;
	
	@Column
	private long winnerPlayerId;
	
	@OneToMany
	private List<Player> quizPlayers;
	
	
	
}
