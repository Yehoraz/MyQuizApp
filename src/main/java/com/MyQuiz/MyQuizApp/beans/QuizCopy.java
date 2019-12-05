package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.CascadeType;
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
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Question> questions;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuizCopy other = (QuizCopy) obj;
		if (questions == null) {
			if (other.questions != null)
				return false;
		} else if (!questions.equals(other.questions))
			return false;
		if (quizName == null) {
			if (other.quizName != null)
				return false;
		} else if (!quizName.equals(other.quizName))
			return false;
		if (quizType != other.quizType)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((questions == null) ? 0 : questions.hashCode());
		result = prime * result + ((quizName == null) ? 0 : quizName.hashCode());
		result = prime * result + ((quizType == null) ? 0 : quizType.hashCode());
		return result;
	}
	
	

}
