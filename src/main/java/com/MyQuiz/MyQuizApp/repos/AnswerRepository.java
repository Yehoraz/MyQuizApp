package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.MyQuiz.MyQuizApp.beans.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>{
	
}
