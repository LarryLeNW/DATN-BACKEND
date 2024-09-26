package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	JavaMailSender mailSender;

	void send(String subject, String body, String recipient) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(recipient); 
		message.setSubject(subject);
		message.setText(body);
		message.setFrom("FASHION@VIETNAM.COM");
		mailSender.send(message);
	}
}
