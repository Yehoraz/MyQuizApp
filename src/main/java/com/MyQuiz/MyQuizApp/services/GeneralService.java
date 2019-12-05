package com.MyQuiz.MyQuizApp.services;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;

@Service
public class GeneralService {

	@Autowired
	private PlayerRepository repository;
	
	public void addPlayer(Player player) throws EntityExistsException {
		if (!repository.existsById(player.getId())) {
			repository.save(player);
		} else {
			throw new EntityExistsException("Player with id: " + player.getId() + " already exists");
		}
	}
	
	public void updatePlayer(Player player) throws EntityNotFoundException {
		if (repository.existsById(player.getId())) {
			repository.save(player);
		} else {
			throw new EntityNotFoundException("Player with id: " + player.getId() + " does not exists");
		}
	}
	
	
}
