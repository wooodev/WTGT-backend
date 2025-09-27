package com.swyp.catsgotogedog.pet.domain.response;

import com.swyp.catsgotogedog.pet.domain.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetProfileResponse {
    private int petId;
    private String name;
    private char gender;
    private LocalDate birth;
    private String type;
    private boolean fierceDog;
    private String size;
    private String imageFilename;
    private String imageUrl;

    public static PetProfileResponse from(Pet pet) {
        return PetProfileResponse.builder()
                .petId(pet.getPetId())
                .name(pet.getName())
                .gender(pet.getGender())
                .birth(pet.getBirth())
                .type(pet.getType())
                .fierceDog(pet.isFierceDog())
                .size(pet.getSizeId().getSize())
                .imageFilename(pet.getImageFilename())
                .imageUrl(pet.getImageUrl())
                .build();
    }
}
