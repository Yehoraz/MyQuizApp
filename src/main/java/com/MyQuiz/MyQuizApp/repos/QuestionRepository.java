package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.MyQuiz.MyQuizApp.beans.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{

	public boolean findByQuestionText(String questionText);
	
}
