package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PlayerMongo {

	@Id
	private long id;
	
	private String firstName;
	
	private String lastName;
	
	private byte age;
	
}
