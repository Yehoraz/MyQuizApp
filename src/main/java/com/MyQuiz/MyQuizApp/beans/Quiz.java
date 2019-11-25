package com.MyQuiz.MyQuizApp.beans;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.MyQuiz.MyQuizApp.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Quiz {
	
	@Id
	private long id;
	
	@Column
	private String quizName;
	
	private long quizManagerId;
//	@OneToOne
//	private QuizManager quizManager;
	
	@Enumerated
	private QuizType quizType;
	
	@OneToOne
	private Player winnerPlayer;
	
	@Column
	private int winnerPlayerScore;
	
	@Column
	private Date quizOpenDate;
	
	@Column
	private Date quizStartDate;
	
	@Column
	private Date quizEndDate;
	
	@Column
	private long quizMaxTimeInMillis;
	
	@Column
	private boolean isQuizPrivate; 
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Question> questions;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Player> players;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<QuizPlayerAnswers> quizPlayerAnswers;

}
