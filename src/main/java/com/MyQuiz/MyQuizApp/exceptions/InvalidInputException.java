package com.MyQuiz.MyQuizApp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidInputException extends Exception {

	private Object item;
	private long id;
	
	public InvalidInputException(Object item, long id, String message) {
		super(message);
		setItem(item);
		setId(id);
	}
	
}
