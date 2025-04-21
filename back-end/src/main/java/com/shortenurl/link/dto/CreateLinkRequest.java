package com.shortenurl.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateLinkRequest {
    @NotBlank(message = "URL must not be blank")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", 
            message = "Invalid URL format")
    private String originalUrl;
} 
