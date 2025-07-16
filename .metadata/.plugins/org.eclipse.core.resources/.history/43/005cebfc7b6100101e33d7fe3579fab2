package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;

    public RestaurantController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
                                ReviewService reviewService, FavoriteRepository favoriteRepository,
                                FavoriteService favoriteService) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "lowestPrice", required = false) Integer lowestPrice,
                        @RequestParam(name = "order", required = false) String order,
                        @RequestParam(name = "category", required = false) String category,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) {

        Page<Restaurant> restaurantPage;

        if (keyword != null && !keyword.isEmpty()) {
            if ("lowestPriceAsc".equals(order)) {
                restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByLowestPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else if (lowestPrice != null) {
            if ("lowestPriceAsc".equals(order)) {
                restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByLowestPriceAsc(lowestPrice, pageable);
            } else {
                restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByCreatedAtDesc(lowestPrice, pageable);
            }
        } else if (category != null && !category.isEmpty()) {
            List<Restaurant> allRestaurants = restaurantRepository.findAll();

            List<Restaurant> filtered = allRestaurants.stream()
                .filter(r -> {
                    if (r.getCategory() == null) return false;
                    String formatted = "、" + r.getCategory().replaceAll("\\s+", "") + "、";
                    String target = "、" + category.trim() + "、";
                    return formatted.contains(target);
                })
                .collect(Collectors.toList());

            restaurantPage = new PageImpl<>(filtered, pageable, filtered.size());
        } else {
            if ("lowestPriceAsc".equals(order)) {
                restaurantPage = restaurantRepository.findAllByOrderByLowestPriceAsc(pageable);
            } else {
                restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("lowestPrice", lowestPrice);
        model.addAttribute("category", category);
        model.addAttribute("order", order);

        List<String> rawCategories = restaurantRepository.findAllDistinctCategories();
        Set<String> categorySet = new TreeSet<>();
        for (String raw : rawCategories) {
            if (raw != null) {
                String[] split = raw.split("、");
                for (String cat : split) {
                    String trimmed = cat.trim();
                    if (!trimmed.isEmpty()) {
                        categorySet.add(trimmed);
                    }
                }
            }
        }
        model.addAttribute("categories", categorySet);

        return "restaurants/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        boolean reviewFlag = false;
        boolean favoriteFlag = false;
        Favorite favorite = null;

        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            model.addAttribute("user", user);
            reviewFlag = reviewService.hasUserAlreadyReviewed(restaurant, user);
            favorite = favoriteService.getFavorite(restaurant, user);
            if (favorite != null) favoriteFlag = true;
        }

        model.addAttribute("reviewFlag", reviewFlag);
        model.addAttribute("favoriteFlag", favoriteFlag);
        model.addAttribute("favorite", favorite);

        List<Review> reviewList = reviewRepository.findTop6ByRestaurantOrderByCreatedAtDesc(restaurant);
        model.addAttribute("reviewList", reviewList);

        int totalCount = (int) reviewRepository.countByRestaurant(restaurant);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reservationInputForm", new ReservationInputForm());

        // ▼ 時間リスト（表示用）
        List<String> timeOptions = generateTimeOptions(restaurant.getOpenTime(), restaurant.getCloseTime());
        model.addAttribute("timeOptions", timeOptions);

        return "restaurants/show";
    }

    @GetMapping("/restaurants/{id}/reservations/input")
    public String input(
        @PathVariable(name = "id") Integer id,
        @ModelAttribute ReservationInputForm reservationInputForm,
        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
        Model model
    ) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        List<String> timeOptions = generateTimeOptions(restaurant.getOpenTime(), restaurant.getCloseTime());
        model.addAttribute("timeOptions", timeOptions);
        model.addAttribute("restaurant", restaurant);

        return "restaurants/reservation_input";
    }

    private List<String> generateTimeOptions(LocalTime openTime, LocalTime closeTime) {
        List<String> timeOptions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime current = openTime;
        boolean isNextDay = openTime.isAfter(closeTime);

        while (true) {
            timeOptions.add(current.format(formatter));
            current = current.plusMinutes(30);

            if (!isNextDay && !current.isBefore(closeTime)) break;
            if (isNextDay && current.equals(closeTime)) break;
        }

        return timeOptions;
    }
}
