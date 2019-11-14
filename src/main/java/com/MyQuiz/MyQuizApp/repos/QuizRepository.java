package com.MyQuiz.MyQuizApp.repos;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{

	public void deleteByQuizEndDateBefore(Date date);
	
}
