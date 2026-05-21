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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrimaryFamilyService {

    @Autowired
    private PrimaryFamilyRepository repository;

    @Value("${supabase.storage.base-url}")
    private String supabaseStorageBaseUrl;

    // ──────────────────────────────────────────────
    // READ operations
    // ──────────────────────────────────────────────

    public Page<PrimaryFamilyDTO> getPrimaryFamilies(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PrimaryFamily> familiesPage;

        if (search != null && !search.trim().isEmpty()) {
            familiesPage = repository.findByNameContainingIgnoreCaseOrHindiNameContainingIgnoreCase(search, search, pageable);
        } else {
            familiesPage = repository.findAll(pageable);
        }

        // ── FIX 1: Batch convert — eliminates N+1 queries ──
        // Collect all entities from the page
        List<PrimaryFamily> entities = familiesPage.getContent();

        // 1. Batch-fetch all parent names in ONE query
        Set<Integer> parentIds = entities.stream()
                .map(PrimaryFamily::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, String> parentNameMap = Collections.emptyMap();
        if (!parentIds.isEmpty()) {
            parentNameMap = repository.findAllById(parentIds).stream()
                    .collect(Collectors.toMap(PrimaryFamily::getId, PrimaryFamily::getName));
        }

        // 2. Batch-check which members have children in ONE query
        Set<Integer> memberIds = entities.stream()
                .map(PrimaryFamily::getId)
                .collect(Collectors.toSet());

        Set<Integer> idsWithChildren = Collections.emptySet();
        if (!memberIds.isEmpty()) {
            idsWithChildren = repository.findByParentIdIn(memberIds).stream()
                    .map(PrimaryFamily::getParentId)
                    .collect(Collectors.toSet());
        }

        // 3. Map to DTOs using the pre-built maps (zero extra queries)
        final Map<Integer, String> finalParentNameMap = parentNameMap;
        final Set<Integer> finalIdsWithChildren = idsWithChildren;

        return familiesPage.map(entity -> {
            PrimaryFamilyDTO dto = new PrimaryFamilyDTO();
            dto.setId(entity.getId());
            dto.setParentId(entity.getParentId());
            dto.setName(entity.getName());
            dto.setHindiName(entity.getHindiName());
            dto.setBirthYear(entity.getBirthYear());
            dto.setProfilePhoto(resolveProfilePhotoUrl(entity.getProfilePhoto(), entity.getLastUpdated()));
            dto.setHasChildren(finalIdsWithChildren.contains(entity.getId()));
            dto.setLastUpdated(entity.getLastUpdated());

            // Use pre-fetched parent name map instead of individual query
            if (entity.getParentId() != null) {
                dto.setParentName(finalParentNameMap.get(entity.getParentId()));
            }
            return dto;
        });
    }

    public PrimaryFamilyDTO getPrimaryFamilyById(Integer id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public java.util.List<PrimaryFamilyDTO> getChildrenOf(Integer parentId) {
        // ── FIX 3: Query children by parent_id directly ──
        // No more reading from a denormalized array
        return repository.findByParentId(parentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // WRITE operations
    // ──────────────────────────────────────────────

    public PrimaryFamilyDTO updateMemberAvatar(Integer id, String avatarPath) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
        member.setProfilePhoto(avatarPath);
        member.setLastUpdated(LocalDateTime.now());
        repository.save(member);
        return convertToDTO(member);
    }

    public PrimaryFamilyDTO updateMemberDetails(Integer id, com.painal.painal_village.dto.MemberUpdateDTO request) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));

        if (request.getName() != null) member.setName(request.getName());
        if (request.getHindiName() != null) member.setHindiName(request.getHindiName());
        if (request.getBirthYear() != null) member.setBirthYear(request.getBirthYear());

        member.setLastUpdated(LocalDateTime.now());
        repository.save(member);
        return convertToDTO(member);
    }

    public PrimaryFamilyDTO addChildToMember(Integer parentId, com.painal.painal_village.dto.MemberUpdateDTO request) {
        // ── FIX 3: Simplified — just set parent_id, no array manipulation ──
        // Verify parent exists
        repository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent not found"));

        // Create the child with parent_id set
        PrimaryFamily child = new PrimaryFamily();
        
        // Manually calculate the next ID because the DB doesn't use auto-increment
        Integer newId = repository.findMaxId() + 1;
        child.setId(newId);
        
        child.setName(request.getName());
        child.setHindiName(request.getHindiName());
        child.setBirthYear(request.getBirthYear());
        child.setParentId(parentId);
        child.setLastUpdated(LocalDateTime.now());

        repository.save(child);

        return convertToDTO(child);
    }

    public void deleteMember(Integer id) {
        PrimaryFamily member = repository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));

        // Validation: Cannot delete if they have children
        // ── FIX 3: Use efficient EXISTS query instead of reading array ──
        if (repository.existsByParentId(id)) {
            throw new IllegalStateException("Cannot delete a member who has children");
        }

        // ── FIX 3: No need to modify parent's children array — it no longer exists ──
        repository.delete(member);
    }

    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    /**
     * Resolves a stored profile photo path (e.g. "312.webp") to its full Supabase public URL
     * and appends a cache-buster based on the lastUpdated timestamp.
     */
    private String resolveProfilePhotoUrl(String path, LocalDateTime lastUpdated) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (path.startsWith("http")) {
            return path;
        }
        String url = supabaseStorageBaseUrl + "/" + path;
        if (lastUpdated != null) {
            long epochMilli = lastUpdated.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return url + "?v=" + epochMilli;
        }
        return url;
    }

    /**
     * Convert a single entity to DTO. Used for single-entity endpoints
     * (getById, update, addChild). Acceptable to make individual queries here
     * since it's only 1 entity at a time.
     */
    private PrimaryFamilyDTO convertToDTO(PrimaryFamily entity) {
        PrimaryFamilyDTO dto = new PrimaryFamilyDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setName(entity.getName());
        dto.setHindiName(entity.getHindiName());
        dto.setBirthYear(entity.getBirthYear());
        dto.setProfilePhoto(resolveProfilePhotoUrl(entity.getProfilePhoto(), entity.getLastUpdated()));
        dto.setHasChildren(repository.existsByParentId(entity.getId()));
        dto.setLastUpdated(entity.getLastUpdated());

        // Resolve parent name (single entity — acceptable)
        if (entity.getParentId() != null) {
            repository.findById(entity.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getName()));
        }

        return dto;
    }
}
