package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {	
	public User findByEmail(String email);
	public Page<User> findByNameLikeOrFuriganaLikeOrEmailLike(String nameKeyword, String furiganaKeyword, String email, Pageable pageable);
}