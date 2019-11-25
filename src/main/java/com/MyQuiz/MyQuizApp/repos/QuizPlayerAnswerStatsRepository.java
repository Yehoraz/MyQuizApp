package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.QuizPlayerAnswerStats;

@Repository
public interface QuizPlayerAnswerStatsRepository extends MongoRepository<QuizPlayerAnswerStats, Long> {

}
