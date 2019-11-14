package com.MyQuiz.MyQuizApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MyQuiz.MyQuizApp.beans.PlayerId;

@Repository
public interface PlayerIdRepository extends JpaRepository<PlayerId, Long>{

}
