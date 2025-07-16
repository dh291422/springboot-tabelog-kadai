package com.example.nagoyameshi.controller;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.nagoyameshi.form.CompanyForm;
import com.example.nagoyameshi.service.CompanyService;

@Controller
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // 表示画面
    @GetMapping("/company")
    public String showCompany(Model model) {
        CompanyForm company = companyService.getLatestCompany();
        model.addAttribute("company", company);
        return "company/company";
    }

    // 編集画面（管理者のみ）
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/company/edit")
    public String editCompany(Model model) {
        CompanyForm form = companyService.getLatestCompany();
        model.addAttribute("companyForm", form);
        return "company/edit";
    }

    // POST（新規レコードとして保存）
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/company/edit")
    public String updateCompany(@ModelAttribute("companyForm") @Valid CompanyForm form,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "company/edit";
        }
        companyService.updateCompany(form);
        return "redirect:/company";
    }

}