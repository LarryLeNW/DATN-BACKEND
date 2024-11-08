package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backend.service.MailService;



@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public String sendMail(@RequestParam String recipient, 
                           @RequestParam String subject, 
                           @RequestParam String body) {
        try {
            mailService.send(subject, body, recipient);
            return "Đã gữi Mail thành công " + recipient;
        } catch (Exception e) {
            return "Chưa gửi được mail: " + e.getMessage();
        }
    }
}