package com.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", columnDefinition = "NVARCHAR(MAX)")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "phone_number")
    String phone_number;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "refresh_token")
    String refresh_token;

    @Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 0")
    int points;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    UserStatusType status = UserStatusType.INACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    LoginType login_type = LoginType.DEFAULT;

    @JsonIgnore
    @ManyToOne
    Role role;

    @OneToMany
    Set<Address> address;

    @OneToMany
    Set<Cart> cart;
    
    @ManyToMany
    @JoinTable(
        name = "voucher_user", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "voucher_id")
    )
	@JsonIgnore
    List<Voucher> vouchers; 


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveries;

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
    private List<Question> questions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionReply> questionReplies;

	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.login_type = LoginType.DEFAULT;
		this.status  = UserStatusType.INACTIVE; 
	}
	
	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	LocalDateTime createdAt;

}
