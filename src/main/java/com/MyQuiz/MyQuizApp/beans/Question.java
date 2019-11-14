package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question {

	@Id
	@GeneratedValue
	private long id;
	
	@Column
	private String questionText;
	
	@Column
	private long correctAnswerId;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Answer> answers;
	
}
