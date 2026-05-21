package com.painal.painal_village.repository;

import com.painal.painal_village.entity.PrimaryFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface PrimaryFamilyRepository extends JpaRepository<PrimaryFamily, Integer> {
    Page<PrimaryFamily> findByNameContainingIgnoreCaseOrHindiNameContainingIgnoreCase(String name, String hindiName, Pageable pageable);
    List<PrimaryFamily> findByParentId(Integer parentId);

    // Fast EXISTS check for hasChildren
    boolean existsByParentId(Integer parentId);

    // Batch: find all members who are children of any of the given parent IDs
    // Used to efficiently determine hasChildren for a whole page of members
    List<PrimaryFamily> findByParentIdIn(Set<Integer> parentIds);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(p.id), 0) FROM PrimaryFamily p")
    Integer findMaxId();
}

