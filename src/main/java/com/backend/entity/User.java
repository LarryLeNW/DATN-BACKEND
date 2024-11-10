package com.backend.entity;

import java.time.LocalDate;

import java.util.List;

import java.util.Optional;
import java.util.Set;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;

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

    @Size(min = 5, max = 5, message = "OTP required 5 characters")
    @Column(name = "otp")
    String otp;

    @Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 0")
    int points;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    UserStatusType status = UserStatusType.INACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    LoginType login_type = LoginType.DEFAULT;

    @ManyToOne
    Role role;

    @OneToMany
    Set<Address> address;

    @OneToMany
    Set<Cart> cart;
    //
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

}
