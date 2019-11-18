package com.MyQuiz.MyQuizApp.services;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.exceptions.InvalidInputException;
//import com.MyQuiz.MyQuizApp.beans.PlayerId;
//import com.MyQuiz.MyQuizApp.repos.PlayerIdRepository;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;

@Service
public class PlayerService {

	@Autowired
	private PlayerRepository repository;

	// this was added for the createplayerid method!!!!!
//	@Autowired
//	private PlayerIdRepository playerIdRepository;

	public void addPlayer(Player player) throws InvalidInputException {
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

	// this method was made for anonymous players, probably should use it after
	// production and upgrades!!
//	private long createPlayerId() {
//		long playerId;
//		
//		do {
//			playerId = (long) Math.abs(Math.random() * 1000000000000000000l);
//		} while (playerIdRepository.getOne(playerId) != null);
//		
//		PlayerId temPlayerId = new PlayerId(playerId);
//		playerIdRepository.save(temPlayerId);
//		return playerId;
//	}

	private boolean validateItemInfo(Object obj) {
		if (obj instanceof Player) {
			Player tempPlayer = (Player) obj;
			if (tempPlayer.getId() < 1 || tempPlayer.getAge() < 1 || tempPlayer.getFirstName().length() < 2
					|| tempPlayer.getLastName().length() < 2) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
