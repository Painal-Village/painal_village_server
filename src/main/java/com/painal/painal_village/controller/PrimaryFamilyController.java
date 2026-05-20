package com.painal.painal_village.controller;

import com.painal.painal_village.dto.PrimaryFamilyDTO;
import com.painal.painal_village.dto.AvatarUpdateDTO;
import com.painal.painal_village.service.PrimaryFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<PrimaryFamilyDTO> getPrimaryFamilyById(@PathVariable Integer id) {
        PrimaryFamilyDTO dto = service.getPrimaryFamilyById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<java.util.List<PrimaryFamilyDTO>> getChildren(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getChildrenOf(id));
    }

    @GetMapping("/{id}/siblings")
    public ResponseEntity<java.util.List<PrimaryFamilyDTO>> getSiblings(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getSiblingsOf(id));
    }

    @PatchMapping("/members/{id}/avatar")
    public ResponseEntity<PrimaryFamilyDTO> updateAvatar(@PathVariable Integer id, @RequestBody AvatarUpdateDTO request) {
        try {
            PrimaryFamilyDTO updatedMember = service.updateMemberAvatar(id, request.getAvatarPath());
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/members/{id}/details")
    public ResponseEntity<PrimaryFamilyDTO> updateMemberDetails(@PathVariable Integer id, @RequestBody com.painal.painal_village.dto.MemberUpdateDTO request) {
        try {
            PrimaryFamilyDTO updatedMember = service.updateMemberDetails(id, request);
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/members/{id}/children")
    public ResponseEntity<PrimaryFamilyDTO> addChild(@PathVariable Integer id, @RequestBody com.painal.painal_village.dto.MemberUpdateDTO request) {
        try {
            PrimaryFamilyDTO newChild = service.addChildToMember(id, request);
            return ResponseEntity.ok(newChild);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer id) {
        try {
            service.deleteMember(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
