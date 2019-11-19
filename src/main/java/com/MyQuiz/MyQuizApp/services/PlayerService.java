package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;

@Service
public class PlayerService {

	@Autowired
	private PlayerRepository repository;

	public void addPlayer(Player player) throws EntityExistsException {
		if (!repository.existsById(player.getId())) {
			repository.save(player);
		} else {
			throw new EntityExistsException("Player with id: " + player.getId() + " already exists");
		}
	}

	public void removePlayer(Player player) throws EntityNotFoundException{
		if (repository.existsById(player.getId())) {
			repository.delete(player);
		} else {
			throw new EntityNotFoundException("Player with id: " + player.getId() + " does not exists");
		}
	}

	public void updatePlayer(Player player) throws EntityNotFoundException {
		if (repository.existsById(player.getId())) {
			repository.save(player);
		} else {
			throw new EntityNotFoundException("Player with id: " + player.getId() + " does not exists");
		}
	}

	public Player getPlayerById(long playerId) {
		return repository.findById(playerId).orElse(null);
	}

	public List<Player> getAllPlayers() {
		if (repository.count() > 0) {
			return repository.findAll();
		} else {
			return null;
		}
	}

}
