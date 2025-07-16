package com.example.nagoyameshi.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {        
        this.verificationTokenRepository = verificationTokenRepository;
    } 
    
    @Transactional
    public void create(User user, String token) {
        // ユーザーに既存トークンがあるか確認
        Optional<VerificationToken> existingOpt = Optional.ofNullable(verificationTokenRepository.findByUser(user));
        
        VerificationToken verificationToken = existingOpt.orElseGet(VerificationToken::new);
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        
        verificationTokenRepository.save(verificationToken);
    }    
    
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
    
    @Transactional
    public void deleteToken(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }
    
    @Transactional
    public VerificationToken recreateToken(User user) {
        // 古いトークンがあれば削除
        VerificationToken existing = verificationTokenRepository.findByUser(user);
        if (existing != null) {
            verificationTokenRepository.delete(existing);
        }

        // 新しいトークンを作成
        String token = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken();
        newToken.setUser(user);
        newToken.setToken(token);
        return verificationTokenRepository.save(newToken);
    }
}
