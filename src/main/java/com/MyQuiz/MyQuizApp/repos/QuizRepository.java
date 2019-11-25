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
	
	public boolean findByQuizManagerIdAndQuizEndDateAfter(long quizManagerId, Date endDate);
	
	public Optional<Quiz> findByQuizManagerIdAndQuizStartDateIsNull(long quizManagerId);
	
	public Optional<Quiz> findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateAfter(long quizManagerId, Date endDate);
	
	public List<Quiz> findByQuizManagerIdAndQuizStartDateIsNotNullAndQuizEndDateBefore(long quizManagerId, Date endDate);
	
	public List<Quiz> findByQuizEndDateIsNull();
	
}
