package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;

@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final StripeService stripeService;

    public ReservationController(
            ReservationRepository reservationRepository,
            RestaurantRepository restaurantRepository,
            StripeService stripeService) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.stripeService = stripeService;
    }

    @GetMapping("/reservations")
    public String index(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
            Model model) {

        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        model.addAttribute("reservationPage", reservationPage);

        return "reservations/index";
    }

    @GetMapping("/restaurants/{id}/reservations/input")
    public String input(
            @PathVariable(name = "id") Integer id,
            @ModelAttribute @Validated ReservationInputForm reservationInputForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            Model model) {

        Restaurant restaurant = restaurantRepository.getReferenceById(id);


        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "restaurants/show";
        }

        model.addAttribute("restaurant", restaurant);
        return "reservations/input";
    }

    @PostMapping("/restaurants/{id}/reservations/confirm")
    public String confirm(
            @PathVariable(name = "id") Integer id,
            @ModelAttribute @Validated ReservationInputForm reservationInputForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            HttpServletRequest httpServletRequest,
            RedirectAttributes redirectAttributes,
            Model model) {

        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "reservations/input";
        }

        // Stripe セッション作成
        String sessionId = stripeService.createStripeSession(httpServletRequest);
        model.addAttribute("sessionId", sessionId);

        // 予約を保存
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setUser(userDetailsImpl.getUser());
        reservation.setCheckinDate(reservationInputForm.getCheckinDate());
        reservation.setReservationTime(reservationInputForm.getReservationTimeAsTime());
        reservation.setNumberOfPeople(reservationInputForm.getNumberOfPeople());
        reservationRepository.save(reservation);

        redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました♡");
        return "redirect:/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancel(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationRepository.findById(id).orElse(null);

        if (reservation == null || !reservation.getUser().getId().equals(userDetailsImpl.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "予約が見つからないか、キャンセルできません。");
            return "redirect:/reservations";
        }

        reservation.setCanceled(true);
        reservationRepository.save(reservation);

        redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました♡");
        return "redirect:/reservations";
    }
}
