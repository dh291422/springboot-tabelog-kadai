package com.example.nagoyameshi.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }    
    
    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByName("ROLE_GENERAL");
        
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);        
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());
        
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());      
        
        userRepository.save(user);
    }
    
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);  
        return user != null;
    }
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }
    
    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true); 
        userRepository.save(user);
    }
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }
    
 // メールアドレスからユーザーを検索する
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // パスワードを更新する
    @Transactional
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    
    public boolean isValidPasswordResetToken(String token) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        return verificationToken != null && verificationToken.getUser().isEnabled();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        if (verificationToken == null) {
            return false;
        }

        User user = verificationToken.getUser();

        if (user == null || !user.isEnabled()) {
            return false;
        }

        // パスワードのハッシュ化と更新
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        verificationTokenService.deleteToken(verificationToken);

        return true;
    }
    
    @Transactional
    public boolean processPasswordResetRequest(String email, String requestUrl) {
        User user = userRepository.findByEmail(email);

        if (user == null || !user.isEnabled()) {
            return false;
        }

        // トークン生成
        String token = UUID.randomUUID().toString();
        verificationTokenService.create(user, token);

        String resetUrl = requestUrl.replace("/request", "") + "?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

        return true;
    }


}