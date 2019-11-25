package com.MyQuiz.MyQuizApp.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.QuizInfo;

@Repository
public interface QuizInfoRepository extends MongoRepository<QuizInfo, Long>{

	public List<QuizInfo> findByWinnerPlayerId(long winnerPlayerId);
	
}
