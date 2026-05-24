package com.painal.painal_village.dto;

import lombok.Data;

@Data
public class MemberReportDto {
    private Long memberId;
    private Boolean incorrectEnglishName;
    private String correctEnglishName;
    private Boolean incorrectHindiName;
    private String correctHindiName;
    private Boolean incorrectDob;
    private String correctDob;
    private Boolean incorrectProfilePhoto;
}
