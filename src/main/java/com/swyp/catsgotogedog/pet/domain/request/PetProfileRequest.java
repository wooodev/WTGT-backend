package com.swyp.catsgotogedog.pet.domain.request;

import com.swyp.catsgotogedog.pet.domain.validation.ValidPetName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PetProfileRequest {

    @NotBlank(message = "반려동물 이름은 필수입니다.")
    @ValidPetName
    private String name;

    @NotBlank(message = "반려동물 성별은 필수입니다.")
    @Pattern(regexp = "[MF]", message = "반려동물 성별은 M(수컷) 또는 F(암컷)이어야 합니다.")
    private String gender;

    @NotNull(message = "반려동물 생년월일은 필수입니다.")
    private LocalDate birth;

    @Size(min = 1, max = 20, message = "반려동물 종류는 최소 1자, 최대 20자까지 입력 가능합니다.")
    @Pattern(regexp = "[가-힣a-zA-Z\\s()]+", message = "반려동물 종류는 한글, 영문, 공백, 소괄호만 입력 가능합니다.")
    @NotBlank(message = "반려동물 종류는 필수입니다.")
    private String type;

    @NotNull(message = "맹견 여부는 필수입니다.")
    private boolean fierceDog;

    @NotBlank(message = "반려동물 크기는 필수입니다.")
    private String size;

    private List<MultipartFile> image;
}
