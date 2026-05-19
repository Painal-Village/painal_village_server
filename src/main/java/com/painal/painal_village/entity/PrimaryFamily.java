package com.painal.painal_village.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "integer[]")
    private List<Integer> children;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
