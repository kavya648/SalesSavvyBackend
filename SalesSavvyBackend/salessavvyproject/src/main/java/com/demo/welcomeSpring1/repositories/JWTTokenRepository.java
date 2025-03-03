package com.demo.welcomeSpring1.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.welcomeSpring1.entities.JWTToken;

import jakarta.transaction.Transactional;


@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {

	//find a token by its value

	Optional<JWTToken> findByToken(String token);


	//custom query to find tokens by user ID
	@Query("SELECT t FROM JWTToken t WHERE t.user.userId = :userId")
	JWTToken findByUserId(@Param("userId")int userId);

	// Custom query to delete tokens by user ID
    @Modifying
	@Transactional
	@Query("DELETE FROM JWTToken t WHERE t.user.userId = :userId")
	void deleteByUserId(@Param("userId") int userId);



}



