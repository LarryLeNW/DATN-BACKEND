package com.backend.entity;

import java.util.Date;

import com.backend.constant.Type.ChatMessageStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message")
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@Column(name = "senderName", columnDefinition = "NVARCHAR(MAX)")
	public String senderName;
	
	@Column(name = "receiverName", columnDefinition = "NVARCHAR(MAX)")
	public String receiverName;
	
	@Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
	public String content;
	
	public Date time;
	
	@Enumerated(EnumType.STRING)
	public ChatMessageStatus status;
}
