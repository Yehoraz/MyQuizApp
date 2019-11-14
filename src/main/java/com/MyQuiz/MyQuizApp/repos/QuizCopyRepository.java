package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.QuizCopy;

@Repository
public interface QuizCopyRepository extends JpaRepository<QuizCopy, Long>{

}
