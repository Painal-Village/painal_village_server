package com.painal.painal_village.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrimaryFamilyDTO {
    private Integer id;
    private Integer parentId;
    private String parentName;
    private String name;
    private String hindiName;
    private String birthYear;
    private String profilePhoto;
    private boolean hasChildren;
    private LocalDateTime lastUpdated;
}
