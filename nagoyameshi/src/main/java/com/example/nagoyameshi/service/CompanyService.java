package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyForm;
import com.example.nagoyameshi.repository.CompanyRepository;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyForm getLatestCompany() {
        Company latest = companyRepository.findLatest();

        CompanyForm form = new CompanyForm();
        form.setId(latest.getId());
        form.setName(latest.getName());
        form.setCeo(latest.getCeo());
        form.setEstablished(latest.getEstablished());
        form.setPostal(latest.getPostal());
        form.setAddress(latest.getAddress());
        form.setBusiness(latest.getBusiness());
        return form;
    }

    public void insertNewCompany(CompanyForm form) {
        Company company = new Company();

        company.setName(form.getName());
        company.setCeo(form.getCeo());
        company.setEstablished(form.getEstablished());
        company.setPostal(form.getPostal());
        company.setAddress(form.getAddress());
        company.setBusiness(form.getBusiness());

        companyRepository.save(company);
    }
    
    public void updateCompany(CompanyForm form) {
        Company company = companyRepository.findById(form.getId())
            .orElseThrow(() -> new IllegalArgumentException("会社が見つかりません"));

        company.setName(form.getName());
        company.setCeo(form.getCeo());
        company.setEstablished(form.getEstablished());
        company.setPostal(form.getPostal());
        company.setAddress(form.getAddress());
        company.setBusiness(form.getBusiness());

        companyRepository.save(company);
    }

}