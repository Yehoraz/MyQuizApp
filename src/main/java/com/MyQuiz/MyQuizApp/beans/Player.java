package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player {

	@Id
	private long id;
	
	private String firstName;
	
	private String lastName;
	
	private byte age;
	
}
