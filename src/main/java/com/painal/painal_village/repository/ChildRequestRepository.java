package com.painal.painal_village.repository;

import com.painal.painal_village.entity.ChildRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRequestRepository extends JpaRepository<ChildRequest, Long> {
}
