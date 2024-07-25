package com.startingblue.fourtooncookie.artwork.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.URL;

public record ArtworkUpdateRequest(
        @NotBlank @Size(max = 255) String title,
        @NotNull URL thumnailUrl) {
}
