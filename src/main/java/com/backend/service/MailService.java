package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void send(String subject, String body, String recipient) {
        MimeMessage message = mailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("FASHION@VIETNAM.COM");

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
