package com.painal.painal_village.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "members_reports")
@Data
@NoArgsConstructor
public class MemberReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "incorrect_english_name")
    private Boolean incorrectEnglishName = false;

    @Column(name = "correct_english_name")
    private String correctEnglishName;

    @Column(name = "incorrect_hindi_name")
    private Boolean incorrectHindiName = false;

    @Column(name = "correct_hindi_name")
    private String correctHindiName;

    @Column(name = "incorrect_dob")
    private Boolean incorrectDob = false;

    @Column(name = "correct_dob")
    private String correctDob;

    @Column(name = "incorrect_profile_photo")
    private Boolean incorrectProfilePhoto = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
