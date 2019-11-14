package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.MyQuiz.MyQuizApp.enums.QuizType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuizCopy {
	
	@Id
	private long id;
	
	@Column
	private String quizName;
	
	@Enumerated
	private QuizType quizType;
	
	@OneToMany
	private List<Question> questions;

}
