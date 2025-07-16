package com.example.nagoyameshi.form;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RestaurantRegisterForm {
    @NotBlank(message = "店舗名を入力してください。")
    private String name;

    private MultipartFile imageFile;

    @NotBlank(message = "説明を入力してください。")
    private String description;

    @NotNull(message = "最低価格帯を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private Integer lowestPrice;

    @NotNull(message = "最高価格帯を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private Integer highestPrice;

    @NotNull(message = "座席数を入力してください。")
    @Min(value = 1, message = "座席数は1席以上に設定してください。")
    private Integer capacity;

    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;

    @NotBlank(message = "住所を入力してください。")
    private String address;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;
    
    @NotBlank(message = "開店時間を入力してください。")
    private String openTime;

    @NotBlank(message = "閉店時間を入力してください。")
    private String closeTime;

    @NotNull(message = "定休日を入力してください。")
    private List<String> regularHoliday;

    @NotBlank(message = "カテゴリを入力してください。")
    @Pattern(regexp = "^[^\\s　。・,./*／－-]+(、[^\\s　。・,./*／－-]+)*$",
    	    message = "複数カテゴリがある場合は「、」(全角句読点)区切りで入力してください。")
    private String category;
    
    private String imageName;

}
