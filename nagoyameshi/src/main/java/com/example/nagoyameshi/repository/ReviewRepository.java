package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findTop6ByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

    Review findByRestaurantAndUser(Restaurant restaurant, User user);

    long countByRestaurant(Restaurant restaurant);

    Page<Review> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant, Pageable pageable);
}