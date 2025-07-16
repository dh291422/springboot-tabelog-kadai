package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public void create(Restaurant restaurant, User user) {

        Favorite existingFavorite = favoriteRepository.findByRestaurantAndUser(restaurant, user);
        if (existingFavorite == null) {
            Favorite favorite = new Favorite();
            favorite.setRestaurant(restaurant);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
        }
    }

    public boolean isFavorite(Restaurant restaurant, User user) {
        return favoriteRepository.findByRestaurantAndUser(restaurant, user) != null;
    }

    public Favorite getFavorite(Restaurant restaurant, User user) {
        return favoriteRepository.findByRestaurantAndUser(restaurant, user);
    }

    @Transactional
    public void delete(Integer favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }
}