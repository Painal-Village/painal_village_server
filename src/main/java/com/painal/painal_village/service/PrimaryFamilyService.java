package com.painal.painal_village.service;

import com.painal.painal_village.dto.PrimaryFamilyDTO;
import com.painal.painal_village.entity.PrimaryFamily;
import com.painal.painal_village.repository.PrimaryFamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PrimaryFamilyService {

    @Autowired
    private PrimaryFamilyRepository repository;

    public Page<PrimaryFamilyDTO> getPrimaryFamilies(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PrimaryFamily> familiesPage;
        
        if (search != null && !search.trim().isEmpty()) {
            familiesPage = repository.findByNameContainingIgnoreCaseOrHindiNameContainingIgnoreCase(search, search, pageable);
        } else {
            familiesPage = repository.findAll(pageable);
        }
        
        return familiesPage.map(this::convertToDTO);
    }

    private PrimaryFamilyDTO convertToDTO(PrimaryFamily entity) {
        PrimaryFamilyDTO dto = new PrimaryFamilyDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setName(entity.getName());
        dto.setHindiName(entity.getHindiName());
        dto.setBirthYear(entity.getBirthYear());
        dto.setProfilePhoto(entity.getProfilePhoto());
        dto.setChildren(entity.getChildren());
        dto.setLastUpdated(entity.getLastUpdated());
        return dto;
    }
}
