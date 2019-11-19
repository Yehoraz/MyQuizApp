package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.SuggestedQuestion;

@Repository
public interface SuggestedQuestionRepository extends JpaRepository<SuggestedQuestion, Long>{

	public boolean findByQuestion(Question question);
	
}
