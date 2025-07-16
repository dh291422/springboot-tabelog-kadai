package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final UserService userService;

    // パスワード再設定リクエスト画面
    @GetMapping("/reset/request")
    public String showResetRequestForm() {
        return "auth/reset";
    }

    // メール送信処理
    @PostMapping("/reset/request")
    public String sendResetEmail(@RequestParam("email") String email, HttpServletRequest request, Model model) {
        String requestUrl = request.getRequestURL().toString();
        boolean result = userService.processPasswordResetRequest(email, requestUrl);

        if (result) {
            model.addAttribute("successMessage", "再設定用のリンクをメールで送信しました。");
        } else {
            model.addAttribute("errorMessage", "有効なユーザーが見つかりませんでした。");
        }

        return "auth/reset";
    }

    // トークン付き再設定画面の表示
    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        boolean isValid = userService.isValidPasswordResetToken(token);

        if (!isValid) {
            model.addAttribute("errorMessage", "このリンクは無効または期限切れです。");
            return "auth/reset";
        }

        model.addAttribute("token", token);
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "auth/reset_password";
    }

    // 新しいパスワードの保存処理
    @PostMapping("/reset")
    public String resetPassword(@ModelAttribute PasswordResetForm form, Model model) {
        boolean result = userService.resetPassword(form.getToken(), form.getPassword());

        if (result) {
            model.addAttribute("successMessage", "パスワードを更新しました。ログインしてください♡");
            return "auth/login";
        } else {
            model.addAttribute("errorMessage", "パスワードの再設定に失敗しました。");
            return "auth/reset";
        }
    }
}