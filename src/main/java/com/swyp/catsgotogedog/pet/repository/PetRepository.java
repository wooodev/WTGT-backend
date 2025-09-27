package com.swyp.catsgotogedog.pet.repository;

import com.swyp.catsgotogedog.pet.domain.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findAllByUser_UserIdOrderByPetId(int userUserId);
    int countByUser_UserId(int userId);
}
