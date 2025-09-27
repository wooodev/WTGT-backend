package com.swyp.catsgotogedog.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;

import java.util.Optional;

public interface PetGuideRepository extends JpaRepository<PetGuide, Integer> {
    boolean existsByContent_ContentId(int contentId);

    Optional<PetGuide> findByContent_ContentId(int contentId);
}
