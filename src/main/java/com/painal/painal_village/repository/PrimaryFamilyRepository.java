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

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(p.id), 0) FROM PrimaryFamily p")
    Integer findMaxId();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO primary_families (id, name, hindi_name, birth_year, parent_id, last_updated) VALUES (:id, :name, :hindiName, :birthYear, :parentId, :lastUpdated)", nativeQuery = true)
    void insertChild(@org.springframework.data.repository.query.Param("id") Integer id,
                     @org.springframework.data.repository.query.Param("name") String name,
                     @org.springframework.data.repository.query.Param("hindiName") String hindiName,
                     @org.springframework.data.repository.query.Param("birthYear") String birthYear,
                     @org.springframework.data.repository.query.Param("parentId") Integer parentId,
                     @org.springframework.data.repository.query.Param("lastUpdated") java.time.LocalDateTime lastUpdated);
}
