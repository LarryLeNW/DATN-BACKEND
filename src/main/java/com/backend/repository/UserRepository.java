package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.constant.Type.UserStatusType;
import com.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndStatusNot(String email, UserStatusType status);

    Optional<User> findByUsername(String username);
}
