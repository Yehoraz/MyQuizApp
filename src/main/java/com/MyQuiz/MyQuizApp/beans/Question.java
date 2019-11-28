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

	@Column(unique = true)
	private String questionText;

	@Column
	private long correctAnswerId;

	@Column
	private boolean isApproved;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Answer> answers;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (answers == null) {
			if (other.answers != null)
				return false;
		}
		if (correctAnswerId != other.correctAnswerId)
			return false;
		if (isApproved != other.isApproved)
			return false;
		if (questionText == null) {
			if (other.questionText != null)
				return false;
		} else if (!questionText.equals(other.questionText))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result + (int) (correctAnswerId ^ (correctAnswerId >>> 32));
		result = prime * result + (isApproved ? 1231 : 1237);
		result = prime * result + ((questionText == null) ? 0 : questionText.hashCode());
		return result;
	}

}
