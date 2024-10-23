package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teams")
@Data
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String description;
	
	private String linkJoin;

	@OneToMany(mappedBy = "User", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<User> users;
	
	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;
	
	@CreationTimestamp
	@Column(name = "update_at")
	LocalDateTime updateAt;

	

}
