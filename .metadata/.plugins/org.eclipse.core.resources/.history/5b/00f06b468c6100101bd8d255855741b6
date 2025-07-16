package com.example.nagoyameshi.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public AdminRestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String index(Model model,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
        @RequestParam(name = "keyword", required = false) String keyword) {

        Page<Restaurant> restaurantPage;
        if (keyword != null && !keyword.isEmpty()) {
            restaurantPage = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
        } else {
            restaurantPage = restaurantRepository.findAll(pageable);
        }

        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);

        return "admin/restaurants/index";
    }

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
        model.addAttribute("minutes", List.of("00", "30"));

        // ✅ 明示的に _csrf を渡す（テンプレートで使うため）
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);

        return "admin/restaurants/register";
    }

    @PostMapping("/create")
    public String create(
        @ModelAttribute("restaurantRegisterForm") @Valid RestaurantRegisterForm restaurantRegisterForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("minutes", List.of("00", "30"));

            // ✅ エラー時にも _csrf を明示的に渡す
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            model.addAttribute("_csrf", csrfToken);

            return "admin/restaurants/register";
        }

        restaurantService.create(restaurantRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        String imageName = restaurant.getImageName();
        List<String> holidays = Arrays.asList(restaurant.getRegularHoliday().split("、"));

        RestaurantEditForm restaurantEditForm = new RestaurantEditForm(
            restaurant.getId(),
            restaurant.getName(),
            null,
            restaurant.getDescription(),
            restaurant.getLowestPrice(),
            restaurant.getHighestPrice(),
            restaurant.getCapacity(),
            restaurant.getPostalCode(),
            restaurant.getAddress(),
            restaurant.getPhoneNumber(),
            restaurant.getOpenTime().format(formatter),
            restaurant.getCloseTime().format(formatter),
            holidays,
            restaurant.getCategory(),
            imageName
        );

        model.addAttribute("imageName", imageName);
        model.addAttribute("restaurantEditForm", restaurantEditForm);
        return "admin/restaurants/edit";
    }

    @PostMapping("/{id}/update")
    public String update(
        @ModelAttribute @Validated RestaurantEditForm restaurantEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/restaurants/edit";
        }

        restaurantService.update(restaurantEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        restaurantRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        model.addAttribute("restaurant", restaurant);
        return "admin/restaurants/show";
    }
}
