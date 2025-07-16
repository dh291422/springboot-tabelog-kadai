package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;

    public ReservationController(
            ReservationRepository reservationRepository,
            RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
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
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = userDetailsImpl.getUser();

        // 有料会員かチェック
        if (!user.isPaidMember()) {
            redirectAttributes.addFlashAttribute("errorMessage", "予約は有料会員のみご利用いただけます。");
            return "redirect:/restaurants/" + id;
        }

        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            model.addAttribute("timeOptions", generateTimeOptions());
            return "restaurants/show";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("timeOptions", generateTimeOptions());
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
            model.addAttribute("user", userDetailsImpl.getUser());
            model.addAttribute("timeOptions", generateTimeOptions());
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "restaurants/show";
        }

        // 予約を保存
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setUser(userDetailsImpl.getUser());
        reservation.setCheckinDate(reservationInputForm.getCheckinDate());
        reservation.setReservationTime(reservationInputForm.getReservationTimeAsTime());
        reservation.setNumberOfPeople(reservationInputForm.getNumberOfPeople());
        reservationRepository.save(reservation);

        // 予約完了フラグ付きでリダイレクト
        return "redirect:/reservations?reserved";
    }

    @GetMapping("/restaurants/{id}/reservations/confirm")
    public String handleGetConfirm(@PathVariable Integer id) {
        return "redirect:/restaurants/" + id + "/reservations/input";
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

    // 予約時間選択肢の生成メソッド
    private List<String> generateTimeOptions() {
        List<String> options = new ArrayList<>();
        LocalTime time = LocalTime.of(11, 0);
        LocalTime closeTime = LocalTime.of(18, 0);

        while (time.isBefore(closeTime)) {
            options.add(String.format("%02d:%02d", time.getHour(), time.getMinute()));
            time = time.plusMinutes(30);
        }

        return options;
    }
}