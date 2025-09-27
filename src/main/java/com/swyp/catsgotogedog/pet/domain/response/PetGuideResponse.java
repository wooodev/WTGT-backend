package com.swyp.catsgotogedog.pet.domain.response;

import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;
import lombok.Builder;

@Builder
public class PetGuideResponse {
    private String accidentPrep;
    private String availableFacility;
    private String providedItem;
    private String etcInfo;
    private String purchasableItem;
    private String allowedPetType;
    private String rentItem;
    private String petPrep;
    private String withPet;

    public static PetGuideResponse from(PetGuide e) {
        return PetGuideResponse.builder()
                .accidentPrep(e.getAccidentPrep())
                .availableFacility(e.getAvailableFacility())
                .providedItem(e.getProvidedItem())
                .etcInfo(e.getEtcInfo())
                .purchasableItem(e.getPurchasableItem())
                .allowedPetType(e.getAllowedPetType())
                .rentItem(e.getRentItem())
                .petPrep(e.getPetPrep())
                .withPet(e.getWithPet())
                .build();
    }
}
