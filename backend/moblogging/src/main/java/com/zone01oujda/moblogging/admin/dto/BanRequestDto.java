package com.zone01oujda.moblogging.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanRequestDto {
    @NotBlank(message = "Ban reason is required")
    private String reason;
    private Boolean isPermanent;
    private Integer durationDays;
}
