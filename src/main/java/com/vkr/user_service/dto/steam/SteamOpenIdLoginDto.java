package com.vkr.user_service.dto.steam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamOpenIdLoginDto {

    @URL
    @NotBlank
    private String ns;

    @URL
    @NotBlank
    private String op_endpoint;

    @URL
    @NotBlank
    private String claimed_id;

    @URL
    @NotBlank
    private String identity;

    @URL
    @NotBlank
    private String return_to;

    @NotBlank
    private String response_nonce;

    @NotBlank
    private String assoc_handle;

    @NotBlank
    @Pattern(regexp = "^\\w+(?:,\\w+)*$")
    private String signed;

    @NotBlank
    private String sig;
}
