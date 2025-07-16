package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("【NagoyaMeshi】パスワード再設定のご案内");
        message.setText(
            "以下のリンクをクリックして、パスワードの再設定を行ってください。\n\n" +
            resetUrl + "\n\n" +
            "※このリンクは一定時間で無効になります。"
        );
        mailSender.send(message);
    }
}
