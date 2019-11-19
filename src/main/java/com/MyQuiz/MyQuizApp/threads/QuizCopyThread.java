package com.MyQuiz.MyQuizApp.threads;

import javax.persistence.EntityExistsException;

import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.services.QuizCopyService;

public class QuizCopyThread implements Runnable {

	private Quiz quiz;
	private QuizCopyService quizCopyService;

	public QuizCopyThread(Quiz quiz, QuizCopyService quizCopyService) {
		this.quiz = quiz;
		this.quizCopyService = quizCopyService;
	}

	@Override
	public void run() {
		if (quiz != null && quizCopyService != null) {
			quiz.getQuestions().forEach(q -> q.getAnswers().forEach(a -> a.setCorrectAnswer(false)));
			QuizCopy tempQuizCopy = new QuizCopy(quiz.getId(), quiz.getQuizName(), quiz.getQuizType(),
					quiz.getQuestions());
			try {
				quizCopyService.addQuizCopy(tempQuizCopy);
			} catch (EntityExistsException e) {
				System.out.println("if it gets here server has a problem of loop need to check how to fix it!!!!");
			}
		}
	}

}
