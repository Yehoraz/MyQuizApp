package com.MyQuiz.MyQuizApp.threads;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.MyQuiz.MyQuizApp.beans.Answer;
import com.MyQuiz.MyQuizApp.beans.Question;
import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
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
			for (Question q : quiz.getQuestions()) {
				for (Answer a : q.getAnswers()) {
					a.setIfCorrectAnswer(false);
				}
			}
			QuizCopy tempQuizCopy = new QuizCopy(quiz.getId(), quiz.getQuizName(), quiz.getQuizType(),
					quiz.getQuestions());
			try {
				quizCopyService.addQuizCopy(tempQuizCopy);
			} catch (EntityExistsException e) {
				try {
					quizCopyService.updateQuizCopy(tempQuizCopy);
				} catch (EntityNotFoundException | InvalidInputException e1) {
					System.out.println("if it gets here server has a problem of loop need to check how to fix it!!!!");
				}
			} catch (InvalidInputException e) {
				System.out.println("user will probably get the response from the original Quiz with invalid input, need to check");
			}
		}
	}

}
