package com.painal.painal_village.controller;

import com.painal.painal_village.dto.MemberReportDto;
import com.painal.painal_village.entity.MemberReport;
import com.painal.painal_village.repository.MemberReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/reports")
@CrossOrigin(origins = "*") // Allows mobile app to call this API without CORS issues
public class MemberReportController {

    @Autowired
    private MemberReportRepository memberReportRepository;

    @PostMapping
    public ResponseEntity<String> reportIncorrectDetails(@RequestBody MemberReportDto dto) {
        if (dto.getMemberId() == null) {
            return ResponseEntity.badRequest().body("Member ID is required");
        }

        MemberReport report = new MemberReport();
        report.setMemberId(dto.getMemberId());
        
        report.setIncorrectEnglishName(dto.getIncorrectEnglishName() != null ? dto.getIncorrectEnglishName() : false);
        report.setCorrectEnglishName(dto.getCorrectEnglishName());
        
        report.setIncorrectHindiName(dto.getIncorrectHindiName() != null ? dto.getIncorrectHindiName() : false);
        report.setCorrectHindiName(dto.getCorrectHindiName());
        
        report.setIncorrectDob(dto.getIncorrectDob() != null ? dto.getIncorrectDob() : false);
        report.setCorrectDob(dto.getCorrectDob());
        
        report.setIncorrectProfilePhoto(dto.getIncorrectProfilePhoto() != null ? dto.getIncorrectProfilePhoto() : false);

        memberReportRepository.save(report);

        return ResponseEntity.ok("Report submitted successfully");
    }
}
