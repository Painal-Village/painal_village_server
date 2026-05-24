package com.painal.painal_village.repository;

import com.painal.painal_village.entity.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
}
