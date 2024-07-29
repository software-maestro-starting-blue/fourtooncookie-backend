package com.startingblue.fourtooncookie.character.dto.response;

import java.net.URL;

public record CharacterResponse(Long id, String characterVisionType, String artworkTitle, URL artworkThumbnailUrl, String name, URL selectionThumbnailUrl) {
}
