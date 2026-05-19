package com.painal.painal_village.controller;

import com.painal.painal_village.dto.PrimaryFamilyDTO;
import com.painal.painal_village.service.PrimaryFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/primary-families")
public class PrimaryFamilyController {

    @Autowired
    private PrimaryFamilyService service;

    @GetMapping
    public ResponseEntity<Page<PrimaryFamilyDTO>> getAllPrimaryFamilies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<PrimaryFamilyDTO> result = service.getPrimaryFamilies(page, size, search);
        return ResponseEntity.ok(result);
    }
}
