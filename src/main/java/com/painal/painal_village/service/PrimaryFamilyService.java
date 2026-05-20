package com.painal.painal_village.service;

import com.painal.painal_village.dto.PrimaryFamilyDTO;
import com.painal.painal_village.entity.PrimaryFamily;
import com.painal.painal_village.repository.PrimaryFamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PrimaryFamilyService {

    @Autowired
    private PrimaryFamilyRepository repository;

    @Value("${supabase.storage.base-url}")
    private String supabaseStorageBaseUrl;

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

    public PrimaryFamilyDTO getPrimaryFamilyById(Integer id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public java.util.List<PrimaryFamilyDTO> getChildrenOf(Integer parentId) {
        PrimaryFamily parent = repository.findById(parentId).orElse(null);
        if (parent == null || parent.getChildren() == null || parent.getChildren().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return repository.findAllById(parent.getChildren())
                .stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<PrimaryFamilyDTO> getSiblingsOf(Integer memberId) {
        PrimaryFamily member = repository.findById(memberId).orElse(null);
        if (member == null || member.getParentId() == null) {
            return java.util.Collections.emptyList();
        }
        return repository.findByParentId(member.getParentId())
                .stream()
                .filter(sibling -> !sibling.getId().equals(memberId))
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public PrimaryFamilyDTO updateMemberAvatar(Integer id, String avatarPath) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
        member.setProfilePhoto(avatarPath);
        member.setLastUpdated(java.time.LocalDateTime.now());
        repository.save(member);
        return convertToDTO(member);
    }

    public PrimaryFamilyDTO updateMemberDetails(Integer id, com.painal.painal_village.dto.MemberUpdateDTO request) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
        
        if (request.getName() != null) member.setName(request.getName());
        if (request.getHindiName() != null) member.setHindiName(request.getHindiName());
        if (request.getBirthYear() != null) member.setBirthYear(request.getBirthYear());
        
        member.setLastUpdated(java.time.LocalDateTime.now());
        repository.save(member);
        return convertToDTO(member);
    }

    public PrimaryFamilyDTO addChildToMember(Integer parentId, com.painal.painal_village.dto.MemberUpdateDTO request) {
        PrimaryFamily parent = repository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent not found"));

        // 1. Manually calculate the next ID (max + 1)
        Integer newId = repository.findMaxId() + 1;

        // 2. Insert the child natively to bypass any sequence mismatch
        repository.insertChild(newId, request.getName(), request.getHindiName(), request.getBirthYear(), parent.getId(), java.time.LocalDateTime.now());
        
        // Fetch the newly inserted child
        PrimaryFamily child = repository.findById(newId).orElseThrow(() -> new RuntimeException("Failed to insert child"));

        // 2. Update the parent's children list
        java.util.List<Integer> currentChildren = parent.getChildren();
        if (currentChildren == null) {
            currentChildren = new java.util.ArrayList<>();
        } else {
            // Because the returned list might be immutable depending on Hibernate's mapping of integer[],
            // we create a new modifiable ArrayList from it.
            currentChildren = new java.util.ArrayList<>(currentChildren);
        }
        currentChildren.add(child.getId());
        parent.setChildren(currentChildren);
        repository.save(parent);

        return convertToDTO(child);
    }

    public void deleteMember(Integer id) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));

        // Validation: Cannot delete if they have children
        if (member.getChildren() != null && !member.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete a member who has children");
        }

        // Cleanup: Remove this member from their parent's children list
        if (member.getParentId() != null) {
            PrimaryFamily parent = repository.findById(member.getParentId()).orElse(null);
            if (parent != null && parent.getChildren() != null) {
                java.util.List<Integer> updatedChildren = new java.util.ArrayList<>(parent.getChildren());
                updatedChildren.remove(id); // Using remove(Object) or remove(Integer)
                parent.setChildren(updatedChildren);
                repository.save(parent);
            }
        }

        repository.delete(member);
    }

    /**
     * Resolves a stored profile photo path (e.g. "312.webp") to its full Supabase public URL
     * and appends a cache-buster based on the lastUpdated timestamp.
     */
    private String resolveProfilePhotoUrl(String path, java.time.LocalDateTime lastUpdated) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (path.startsWith("http")) {
            return path;
        }
        String url = supabaseStorageBaseUrl + "/" + path;
        if (lastUpdated != null) {
            long epochMilli = lastUpdated.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            return url + "?v=" + epochMilli;
        }
        return url;
    }

    private PrimaryFamilyDTO convertToDTO(PrimaryFamily entity) {
        PrimaryFamilyDTO dto = new PrimaryFamilyDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setName(entity.getName());
        dto.setHindiName(entity.getHindiName());
        dto.setBirthYear(entity.getBirthYear());
        dto.setProfilePhoto(resolveProfilePhotoUrl(entity.getProfilePhoto(), entity.getLastUpdated()));
        dto.setChildren(entity.getChildren());
        dto.setLastUpdated(entity.getLastUpdated());

        // Resolve parent name
        if (entity.getParentId() != null) {
            repository.findById(entity.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getName()));
        }

        return dto;
    }
}
