package com.MyQuiz.MyQuizApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.repos.QuizPlayerAnswerStatsRepository;

@Service
public class QuizPlayerAnswerStatsService {

	@Autowired
	private QuizPlayerAnswerStatsRepository quizPlayerAnswerStatsRepository;
	
}
