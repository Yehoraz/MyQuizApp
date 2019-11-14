package com.MyQuiz.MyQuizApp.messages;

import java.util.Locale;
import java.util.Locale.Builder;

public class QuizAppMessages {

	
	//need to check!!
	private Builder locale;
	private String message;
	
	public QuizAppMessages(String userLocaleCountry, String message) {
		locale = new Locale.Builder();
		locale.setLanguage("en");
		locale.setRegion("US");
		locale.build();
	}
	
}
