package com.example.nagoyameshi.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; // ← これが大事！！

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
public class HomeController {

    private final RestaurantRepository restaurantRepository;

    public HomeController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/")
    public String index(
        @RequestParam(name = "cancelled", required = false) String cancelled, 
        @AuthenticationPrincipal UserDetailsImpl loginUser,
        Model model) {
    	
    	if (loginUser != null) {
            model.addAttribute("isPremium", loginUser.getUser().getSubscriptionId() != null);
    	}                
        
        List<Restaurant> newRestaurants = restaurantRepository.findTop10ByOrderByCreatedAtDesc();
        model.addAttribute("newRestaurants", newRestaurants);
        
        List<String> rawCategories = restaurantRepository.findAllDistinctCategories();
        Set<String> categorySet = rawCategories.stream()
            .filter(c -> c != null && !c.isEmpty())
            .flatMap(c -> Arrays.stream(c.split("、|,|\\s*，\\s*")))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toCollection(TreeSet::new));

        model.addAttribute("categories", categorySet);

        return "index";
    }
}
