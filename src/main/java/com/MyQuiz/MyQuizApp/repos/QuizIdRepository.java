package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.QuizId;

@Repository
public interface QuizIdRepository extends JpaRepository<QuizId, Long> {
	
}
