package com.swyp.catsgotogedog.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.review.domain.entity.ReportReason;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Integer> {
}
