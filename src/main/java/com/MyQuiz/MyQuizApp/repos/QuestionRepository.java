package com.MyQuiz.MyQuizApp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.MyQuiz.MyQuizApp.beans.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{

	public boolean existsByQuestionText(String questionText);
	
	public List<Question> findByIsApproved(boolean flag);
	
	public void deleteByQuestionTextAndIsApproved(String questionText, boolean flag);
	
}
