package com.MyQuiz.MyQuizApp.beans;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
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
	private long playerId;
	
	//generate by Server Side
	private int score;
	
	//generate by Server Side
	private long completionTime;
	
	@ElementCollection(targetClass = Long.class)
	@MapKeyColumn
	@Column
	private Map<Long, Long> playerAnswers;
	
}
