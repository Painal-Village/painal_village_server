package com.painal.painal_village.repository;

import com.painal.painal_village.entity.PrimaryFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PrimaryFamilyRepository extends JpaRepository<PrimaryFamily, Integer> {
    Page<PrimaryFamily> findByNameContainingIgnoreCaseOrHindiNameContainingIgnoreCase(String name, String hindiName, Pageable pageable);
    java.util.List<PrimaryFamily> findByParentId(Integer parentId);
}
