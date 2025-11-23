package com.guangruan.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
