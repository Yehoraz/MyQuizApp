package com.MyQuiz.MyQuizApp.threads;

import org.aspectj.lang.JoinPoint;

public class QuizLoggerThread implements Runnable{

	private JoinPoint jp;
	private Exception e;
	
	public QuizLoggerThread(JoinPoint jp, Exception e) {
		this.jp = jp;
		this.e = e;
	}
	
	@Override
	public void run() {
		System.out.println("Error type: " + jp.getKind() + "\n" + "From: " + jp.getTarget() + "\n" + "Error Signature: "
				+ jp.getSignature() + "\n" + "Error Message: " + e.getMessage() + "\n" + "Error StackTrace: "
				+ parseStackTrace(e.getStackTrace()));
		
	}
	
	private StringBuilder parseStackTrace(StackTraceElement[] stackTrace) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < stackTrace.length; i++) {
			stringBuilder.append("\n" + stackTrace[i]);
		}
		return stringBuilder;
	}

	
}
