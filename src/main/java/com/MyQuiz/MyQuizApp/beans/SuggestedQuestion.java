package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SuggestedQuestion {

	@Id
	@GeneratedValue
	private long id;
	
	@Column
	private long PlayerId;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Question question;
	
}
