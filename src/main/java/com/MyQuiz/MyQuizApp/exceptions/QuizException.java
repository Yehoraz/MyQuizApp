package com.MyQuiz.MyQuizApp.exceptions;

import com.MyQuiz.MyQuizApp.enums.QuizExceptionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizException extends Exception {

	private Object item;
	private long id;
	private QuizExceptionType exceptionType;

	public QuizException(Object item, long id, QuizExceptionType exceptionType, String message) {
		super(message);
		setItem(item);
		setId(id);
		setExceptionType(exceptionType);
	}

}
