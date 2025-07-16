package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public void create(RestaurantRegisterForm restaurantForm) {
        Restaurant restaurant = new Restaurant();
        MultipartFile imageFile = restaurantForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            restaurant.setImageName(hashedImageName);
        }

        restaurant.setName(restaurantForm.getName());
        restaurant.setDescription(restaurantForm.getDescription());
        restaurant.setLowestPrice(restaurantForm.getLowestPrice());
        restaurant.setHighestPrice(restaurantForm.getHighestPrice());
        restaurant.setCapacity(restaurantForm.getCapacity());
        restaurant.setPostalCode(restaurantForm.getPostalCode());
        restaurant.setAddress(restaurantForm.getAddress());
        restaurant.setPhoneNumber(restaurantForm.getPhoneNumber());

        // 時刻形式（HH:mm）で保存
        LocalTime openTime = parseFlexibleTime(restaurantForm.getOpenTime());
        LocalTime closeTime = parseFlexibleTime(restaurantForm.getCloseTime());

        restaurant.setOpenTime(openTime);
        restaurant.setCloseTime(closeTime);

        restaurant.setRegularHoliday(String.join("、", restaurantForm.getRegularHoliday()));

        restaurant.setCategory(restaurantForm.getCategory());

        if (openTime.isAfter(closeTime)) {
            // 翌日営業か判定
            LocalTime limit = LocalTime.of(5, 0); // 05:00

            if (closeTime.isAfter(limit)) {
                throw new IllegalArgumentException("閉店時間が開店時間より前の場合、閉店時間は朝05:00までにしてください。");
            }
        }

        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void update(RestaurantEditForm restaurantEditForm) {
        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
        MultipartFile imageFile = restaurantEditForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            restaurant.setImageName(hashedImageName);
        }

        restaurant.setName(restaurantEditForm.getName());
        restaurant.setDescription(restaurantEditForm.getDescription());
        restaurant.setLowestPrice(restaurantEditForm.getLowestPrice());
        restaurant.setHighestPrice(restaurantEditForm.getHighestPrice());
        restaurant.setCapacity(restaurantEditForm.getCapacity());
        restaurant.setPostalCode(restaurantEditForm.getPostalCode());
        restaurant.setAddress(restaurantEditForm.getAddress());
        restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());

        // 時刻形式に整形
        LocalTime openTime = parseFlexibleTime(restaurantEditForm.getOpenTime());
        LocalTime closeTime = parseFlexibleTime(restaurantEditForm.getCloseTime());

        restaurant.setOpenTime(openTime);
        restaurant.setCloseTime(closeTime);

        restaurant.setRegularHoliday(String.join("、", restaurantEditForm.getRegularHoliday()));
        restaurant.setCategory(restaurantEditForm.getCategory());

        // バリデーション（更新時も）
        if (openTime.isAfter(closeTime)) {
            // 翌日営業か判定
            LocalTime limit = LocalTime.of(5, 0); // 05:00

            if (closeTime.isAfter(limit)) {
                throw new IllegalArgumentException("閉店時間が開店時間より前の場合、閉店時間は朝05:00までにしてください。");
            }
        }

        restaurantRepository.save(restaurant);
    }

    public String generateNewFileName(String fileName) {
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }

    // 画像ファイルを指定パスにコピー
    public void copyImageFile(MultipartFile imageFile, Path filePath) {
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalTime parseFlexibleTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}