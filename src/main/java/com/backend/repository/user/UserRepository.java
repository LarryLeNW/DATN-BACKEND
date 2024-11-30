package com.backend.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.backend.constant.Type.UserStatusType;
import com.backend.entity.Product;
import com.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);
    
    User findByEmail(String email);
    
    Optional<User> findByEmailAndStatusNot(String email, UserStatusType status);

    Optional<User> findByUsername(String username);
    
}
