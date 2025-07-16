package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.nagoyameshi.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

	
    @Query("SELECT c FROM Company c ORDER BY c.id DESC LIMIT 1")
    Company findLatest();
}