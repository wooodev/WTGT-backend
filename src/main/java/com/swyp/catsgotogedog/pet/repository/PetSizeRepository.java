package com.swyp.catsgotogedog.pet.repository;

import com.swyp.catsgotogedog.pet.domain.entity.PetSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetSizeRepository extends JpaRepository<PetSize, Integer> {
    Optional<PetSize> findBySize(String size);
}
