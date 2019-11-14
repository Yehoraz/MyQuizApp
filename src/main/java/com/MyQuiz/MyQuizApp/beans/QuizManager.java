package com.MyQuiz.MyQuizApp.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class QuizManager {

	@Id
	private long id;
	
}
