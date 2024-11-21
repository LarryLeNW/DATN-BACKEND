package com.backend.entity;

import java.util.Collection;
import java.util.List;

import com.backend.dto.response.auth.RoleResponse.ModuleDTO;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;



}