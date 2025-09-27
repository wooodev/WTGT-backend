package com.swyp.catsgotogedog.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformation;

public interface RecurInformationRepository extends JpaRepository<RecurInformation, Integer> {
}
