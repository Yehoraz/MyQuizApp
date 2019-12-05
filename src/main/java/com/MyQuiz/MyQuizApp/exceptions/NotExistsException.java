package com.MyQuiz.MyQuizApp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotExistsException extends Exception {

	private Object item;
	private long id;

	public NotExistsException(Object item, long id, String message) {
		super(message);
		setItem(item);
		setId(id);
	}

}
