package com.painal.painal_village.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "primary_families")
public class PrimaryFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    private String name;

    @Column(name = "hindi_name")
    private String hindiName;

    @Column(name = "birth_year")
    private String birthYear;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
