package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.MyQuiz.MyQuizApp.beans.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
	
}
