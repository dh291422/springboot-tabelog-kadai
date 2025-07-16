package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final StripeService stripeService;

    @GetMapping("/subscription")
    public String showSubscriptionPage(HttpServletRequest request, Model model) {
        String sessionId = stripeService.createStripeSession(request);

        if (sessionId == null || sessionId.isEmpty()) {
            model.addAttribute("errorMessage", "Stripeセッションの作成に失敗しました。");
            return "premium/error";
        }

        model.addAttribute("sessionId", sessionId);
        return "premium/register";
    }

    @GetMapping("/subscription/success")
    public String subscriptionSuccess(
            @AuthenticationPrincipal UserDetailsImpl loginUser,
            HttpServletRequest request) {

        stripeService.handleSuccess(request, loginUser.getUser());
        return "premium/success";
    }

    @GetMapping("/subscription/portal")
    public String redirectToStripePortal(@AuthenticationPrincipal UserDetailsImpl loginUser) {
        String customerId = loginUser.getUser().getStripeCustomerId();

        if (customerId == null || customerId.isEmpty()) {
            return "premium/error";
        }

        String returnUrl = "http://localhost:8080/subscription/edit";
        String portalUrl = stripeService.createCustomerPortalSession(customerId, returnUrl);

        return "redirect:" + portalUrl;
    }

    @GetMapping("/subscription/cancel")
    public String subscriptionCancel() {
        return "premium/cancel";
    }

    @GetMapping("/subscription/cancellation")
    public String showCancelPage() {
        return "premium/cancellation";
    }

    @PostMapping("/subscription/cancellation")
    public String cancelSubscription(@AuthenticationPrincipal UserDetailsImpl loginUser) {
        stripeService.cancelSubscription(loginUser.getUser());
        return "premium/cancel";
    }

    @GetMapping("/subscription/edit")
    public String showEditCompletePage() {
        return "premium/edit";
    }

    @GetMapping("/subscription/manage")
    public String showManagePage() {
        return "premium/manage";
    }
}
