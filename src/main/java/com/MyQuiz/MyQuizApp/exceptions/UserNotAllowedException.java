package com.MyQuiz.MyQuizApp.exceptions;

public class UserNotAllowedException extends Exception{

	private long userId;
	private String exceptionReason;
	
	public UserNotAllowedException(long userId, String exceptionReason, String message) {
		super(message);
		this.userId = userId;
		this.exceptionReason = exceptionReason;
	}
	
}
