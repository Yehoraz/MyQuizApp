package com.MyQuiz.MyQuizApp.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class QuizInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true)
	private long quizId;

	private String quizName;

	private long winnerPlayerId;

	private int winnerPlayerScore;

	@OneToMany(cascade = CascadeType.ALL)
	private List<PlayerMongo> quizPlayers;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuizInfo other = (QuizInfo) obj;
		if (quizId != other.quizId)
			return false;
		if (quizName == null) {
			if (other.quizName != null)
				return false;
		} else if (!quizName.equals(other.quizName))
			return false;
		if (quizPlayers == null) {
			if (other.quizPlayers != null)
				return false;
		} else if (!quizPlayers.equals(other.quizPlayers))
			return false;
		if (winnerPlayerId != other.winnerPlayerId)
			return false;
		if (winnerPlayerScore != other.winnerPlayerScore)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (quizId ^ (quizId >>> 32));
		result = prime * result + ((quizName == null) ? 0 : quizName.hashCode());
		result = prime * result + ((quizPlayers == null) ? 0 : quizPlayers.hashCode());
		result = prime * result + (int) (winnerPlayerId ^ (winnerPlayerId >>> 32));
		result = prime * result + winnerPlayerScore;
		return result;
	}

}
