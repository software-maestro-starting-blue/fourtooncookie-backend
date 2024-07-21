package com.startingblue.fourtooncookie.character.dto.response;

import java.net.URL;

public record CharacterResponse(Long id, String modelType, String name, URL selectionThumbnailUrl) {
}
