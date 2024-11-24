package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.entity.Message;
import com.backend.repository.MessageRepository;

@Controller 
public class WebSocketController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	MessageRepository messageRepository;

	@MessageMapping("/message")
	@SendTo("/chatroom/public")
	public Message receiveMessage(@Payload Message message) {
		System.out.println("chat public "+message);
		return message;
	}

	@MessageMapping("/private-message")
	public Message recMessage(@Payload Message message) {
		simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
		System.out.println(message.toString());
		return message;
	}
	@PostMapping("/messages")
	public ResponseEntity<Message> saveMessage(@RequestBody Message message) {
	    messageRepository.save(message);
	    return ResponseEntity.ok(message);
	}
	@GetMapping("/messages")
	public ResponseEntity<List<Message>> getMessages() {
	    List<Message> messages = messageRepository.findAll();
	    return ResponseEntity.ok(messages);
	}


}
