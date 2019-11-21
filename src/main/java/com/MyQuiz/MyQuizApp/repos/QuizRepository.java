package com.MyQuiz.MyQuizApp.repos;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{

	public void deleteByQuizEndDateBefore(Date date);
	
	public boolean findByQuizManagerIdAndQuizEndDateIsNotNull(long quizManagerId);
	
	public Optional<Quiz> findByQuizManagerIdAndQuizStartDateIsNull(long quizManagerId);
	
	public Optional<Quiz> findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateIsNull(long quizManagerId);
	
	public List<Quiz> findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateIsNotNull(long quizManagerId);
	
}
