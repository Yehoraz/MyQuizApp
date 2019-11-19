package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column
	private String answerText;
	
	@Column
	private boolean isCorrectAnswer;
	
}
