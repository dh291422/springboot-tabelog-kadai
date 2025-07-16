package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
    
    public Page<Restaurant> findAll(Pageable pageable);
    public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<Restaurant> findByNameLikeOrAddressLikeOrderByLowestPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByCreatedAtDesc(Integer lowestPrice, Pageable pageable);
    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByLowestPriceAsc(Integer lowestPrice, Pageable pageable);
    public Page<Restaurant> findByCategoryLikeOrderByCreatedAtDesc(String category, Pageable pageable);
    public Page<Restaurant> findByCategoryLikeOrderByLowestPriceAsc(String category, Pageable pageable);
    public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<Restaurant> findAllByOrderByLowestPriceAsc(Pageable pageable);
    
    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT DISTINCT r.category FROM Restaurant r WHERE r.category IS NOT NULL")
    List<String> findAllDistinctCategories();
    
}