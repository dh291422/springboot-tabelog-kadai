package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    @Query("SELECT f FROM Favorite f JOIN FETCH f.restaurant WHERE f.user = :user ORDER BY f.createdAt DESC")
    Page<Favorite> findByUserWithRestaurant(@Param("user") User user, Pageable pageable);

    Favorite findByRestaurantAndUser(Restaurant restaurant, User user);

	Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
