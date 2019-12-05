package com.MyQuiz.MyQuizApp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizServerException extends Exception {

	private Object item;
	private String methodThrown;

	public QuizServerException(Object item, String methodThrown, String message) {
		super(message);
		setItem(item);
		setMethodThrown(methodThrown);
	}

}
