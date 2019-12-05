package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.QuizInfo;
import com.MyQuiz.MyQuizApp.repos.QuizInfoRepository;

@Service
public class QuizInfoService {

	@Autowired
	private QuizInfoRepository quizInfoRepository;
	
	public QuizInfo addQuizInfo(QuizInfo quizInfo) {
		return quizInfoRepository.save(quizInfo);
	}
	
	// need to transfer to AdminService
	public void removeQuizInfo(long quizId) {
		quizInfoRepository.deleteById(quizId);
	}
	
	// need to transfer to AdminService
	public void removeAllQuizInfo() {
		quizInfoRepository.deleteAll();
	}
	
	public QuizInfo getQuizInfo(long quizId) {
		return quizInfoRepository.findById(quizId).orElse(null);
	}
	
	// need to transfer to AdminService
	public List<QuizInfo> getAllQuizInfos(){
		return quizInfoRepository.findAll();
	}
	
}
