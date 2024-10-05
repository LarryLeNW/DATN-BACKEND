package com.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "attributes")
@Data
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "NVARCHAR(MAX)")
    private String name;

    @OneToMany(mappedBy = "attribute")
    private List<AttributeOption> attributeOptions;

    // Getters and Setters
}
