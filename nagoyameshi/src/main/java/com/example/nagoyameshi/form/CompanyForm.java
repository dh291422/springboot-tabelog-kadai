package com.example.nagoyameshi.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CompanyForm {
    private Long id;
    private String name;
    private String ceo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate established;

    private String postal;
    private String address;
    private String business;
}