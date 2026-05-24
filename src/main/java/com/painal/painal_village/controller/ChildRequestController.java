package com.painal.painal_village.controller;

import com.painal.painal_village.dto.ChildRequestDto;
import com.painal.painal_village.entity.ChildRequest;
import com.painal.painal_village.repository.ChildRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/requests/child")
@CrossOrigin(origins = "*") // Public API
public class ChildRequestController {

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @PostMapping
    public ResponseEntity<String> submitChildRequest(@RequestBody ChildRequestDto dto) {
        if (dto.getMemberId() == null || dto.getChildName() == null || dto.getChildDob() == null) {
            return ResponseEntity.badRequest().body("Member ID, Child Name, and Child DOB are required");
        }

        ChildRequest request = new ChildRequest();
        request.setMemberId(dto.getMemberId());
        request.setChildName(dto.getChildName());
        request.setChildDob(dto.getChildDob());

        childRequestRepository.save(request);

        return ResponseEntity.ok("Child request submitted successfully");
    }
}
