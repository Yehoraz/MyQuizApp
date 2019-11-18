package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;

@Service
public class PlayerService {

	@Autowired
	private PlayerRepository repository;

	public void addPlayer(Player player) throws InvalidInputException, EntityExistsException {
		if (validateItemInfo(player)) {
			if (!repository.existsById(player.getId())) {
				repository.save(player);
			} else {
				throw new EntityExistsException("Player with id: " + player.getId() + " already exists");
			}
		} else {
			throw new InvalidInputException(player.getId(), player.getFirstName() + " " + player.getLastName(),
					"validateItemInfo() returned false", "Player info is Invalid!");
		}
	}

	public void removePlayer(Player player) throws InvalidInputException {
		if (validateItemInfo(player)) {
			if (repository.existsById(player.getId())) {
				repository.delete(player);
			} else {
				throw new EntityNotFoundException("Player with id: " + player.getId() + " does not exists");
			}
		} else {
			throw new InvalidInputException(player.getId(), player.getFirstName() + " " + player.getLastName(),
					"validateItemInfo() returned false", "Player info is Invalid!");
		}
	}

	public void updatePlayer(Player player) throws InvalidInputException {
		if (validateItemInfo(player)) {
			if (repository.existsById(player.getId())) {
				repository.save(player);
			} else {
				throw new EntityNotFoundException("Player with id: " + player.getId() + " does not exists");
			}
		} else {
			throw new InvalidInputException(player.getId(), player.getFirstName() + " " + player.getLastName(),
					"validateItemInfo() returned false", "Player info is Invalid!");
		}
	}

	public Player getPlayerById(long player_id) {
			return repository.findById(player_id).orElse(null);
	}

	public List<Player> getAllPlayers() {
		if (repository.count() > 0) {
			return repository.findAll();
		} else {
			throw new EntityNotFoundException("Player database is empty");
		}
	}
	
	private boolean validateItemInfo(Object obj) {
		if (obj instanceof Player) {
			Player tempPlayer = (Player) obj;
			if (tempPlayer.getId() < 1 || tempPlayer.getAge() < 0 || tempPlayer.getFirstName().length() < 1
					|| tempPlayer.getLastName().length() < 1) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
}
