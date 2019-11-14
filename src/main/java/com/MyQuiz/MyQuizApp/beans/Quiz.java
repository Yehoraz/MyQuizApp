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
	
	@OneToOne
	private QuizManager quizManager;
	
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
	
	@OneToMany
	private List<Player> players;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<QuizPlayerAnswers> quizPlayerAnswers;
	
//	//represent the quiz completion time, key is the player_id and the value is the completion time in millies.
//	@OneToMany
//	private HashMap<Long, Long> playersTimeToCompleteInMillies;
	

}
