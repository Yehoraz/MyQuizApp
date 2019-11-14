package com.MyQuiz.MyQuizApp.exceptions;

public class InvalidInputException extends Exception{

	private long itemId;
	private String itemName;
	private String exceptionReason;
	
	public InvalidInputException(long itemId, String itemName, String exceptionReasion, String message) {
		super(message);
		this.itemId = itemId;
		this.itemName = itemName;
		this.exceptionReason = exceptionReasion;
	}
	
}
