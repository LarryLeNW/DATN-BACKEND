package com.backend.entity.test;


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

    private String name;

    @OneToMany(mappedBy = "attribute")
    private List<AttributeOption> attributeOptions;

    // Getters and Setters
}
