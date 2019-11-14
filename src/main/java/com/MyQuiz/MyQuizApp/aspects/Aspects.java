package com.MyQuiz.MyQuizApp.aspects;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.threads.QuizLoggerThread;

@Component
@Aspect
public class Aspects {
	
	Thread t1 = null;

	@AfterThrowing(pointcut = "execution(* com.MyQuiz.MyQuizApp.controllers..*(..))", throwing = "e")
	public void errorLogger(JoinPoint jp, EntityNotFoundException e) {
		t1 = new Thread(new QuizLoggerThread(jp, e));
		t1.start();
	}

	@AfterThrowing(pointcut = "execution(* com.MyQuiz.MyQuizApp.controllers..*(..))", throwing = "e")
	public void errorLogger(JoinPoint jp, EntityExistsException e) {
		t1 = new Thread(new QuizLoggerThread(jp, e));
		t1.start();
	}

	@AfterThrowing(pointcut = "execution(* com.MyQuiz.MyQuizApp.controllers..*(..))", throwing = "e")
	public void errorLogger(JoinPoint jp, InvalidInputException e) {
		t1 = new Thread(new QuizLoggerThread(jp, e));
		t1.start();
	}

	@AfterThrowing(pointcut = "execution(* com.MyQuiz.MyQuizApp.controllers..*(..))", throwing = "e")
	public void errorLogger(JoinPoint jp, Exception e) {
		if (!(e instanceof EntityNotFoundException || e instanceof EntityExistsException
				|| e instanceof InvalidInputException)) {
			t1 = new Thread(new QuizLoggerThread(jp, e));
			t1.start();
		}
	}

	

}
