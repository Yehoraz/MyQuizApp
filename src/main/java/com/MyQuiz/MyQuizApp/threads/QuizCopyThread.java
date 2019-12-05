package com.MyQuiz.MyQuizApp.threads;

import com.MyQuiz.MyQuizApp.beans.Quiz;
import com.MyQuiz.MyQuizApp.beans.QuizCopy;
import com.MyQuiz.MyQuizApp.repos.QuizCopyRepository;

public class QuizCopyThread implements Runnable {

	private Quiz quiz;
	private QuizCopyRepository quizCopyRepository;

	public QuizCopyThread(Quiz quiz, QuizCopyRepository quizCopyRepository) {
		this.quiz = quiz;
		this.quizCopyRepository = quizCopyRepository;
	}

	@Override
	public void run() {
		if (quiz != null && quizCopyRepository != null) {
			for (int i = 0; i < quiz.getQuestions().size(); i++) {
				for (int j = 0; j < quiz.getQuestions().get(i).getAnswers().size(); j++) {
					quiz.getQuestions().get(i).getAnswers().get(j).setCorrectAnswer(false);
				}
			}

			QuizCopy tempQuizCopy = new QuizCopy(quiz.getId(), quiz.getQuizName(), quiz.getQuizType(),
					quiz.getQuestions());

			if (!quizCopyRepository.existsById(tempQuizCopy.getId())) {
				quizCopyRepository.save(tempQuizCopy);
			} else {
				// some exception maybe? how to handle parallel exception??
			}
		}
	}

}
