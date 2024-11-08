package com.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "attribute_options")
@Data
public class AttributeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    @JsonIgnore
    private Attribute attribute;

    @Column(name = "value", columnDefinition = "NVARCHAR(MAX)")
    private String value;

    @OneToMany(mappedBy = "attributeOption")
    private List<AttributeOptionSku> attributeOptionSkus;

    // Getters and Setters
}
