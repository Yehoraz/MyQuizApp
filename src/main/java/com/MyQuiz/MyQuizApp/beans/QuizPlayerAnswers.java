package com.MyQuiz.MyQuizApp.beans;

import java.util.Map;

import javax.persistence.CascadeType;
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
public class QuizPlayerAnswers {

	@Id
	private long player_id;
	
	//generate by Server Side
	private int score;
	
	//generate by Server Side
	private long completionTime;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Map<Long, Long> playerAnswers;
	
}
