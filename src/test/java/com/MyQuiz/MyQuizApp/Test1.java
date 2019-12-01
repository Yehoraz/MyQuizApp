package com.MyQuiz.MyQuizApp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.MyQuiz.MyQuizApp.beans.Player;
import com.MyQuiz.MyQuizApp.repos.PlayerRepository;

public final class Test1 {
	
	PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
		
	@Test
	public void check1() {
		Player player = new Player(1232l, "ma ze", "mi ze", (byte)32);
		playerRepository.save(player);
		playerRepository.findById(1232l).orElse(null);
		System.out.println("player is: " + player);
		System.out.println(playerRepository);
	}

}
