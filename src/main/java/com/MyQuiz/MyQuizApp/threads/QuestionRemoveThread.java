package com.MyQuiz.MyQuizApp.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.MyQuiz.MyQuizApp.repos.QuestionRepository;

@Component
public class QuestionRemoveThread {

	@Autowired
	private QuestionRepository questionRepository;

	@Scheduled(fixedRate = (1000 * 60 * 60 * 24 * 7))
	private void task() {
		questionRepository.deleteByQuestionTextAndIsApproved("remove", false);
	}

}
