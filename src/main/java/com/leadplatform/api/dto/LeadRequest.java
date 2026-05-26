package com.leadplatform.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequest {

    @NotBlank(message = "leadId is required")
    private String leadId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "mobile is required")
    @Pattern(regexp = "\\d{10}", message = "mobile must be a 10-digit number")
    private String mobile;

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    private String email;
}
