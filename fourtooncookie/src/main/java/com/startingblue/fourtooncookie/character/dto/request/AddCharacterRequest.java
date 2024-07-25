package com.startingblue.fourtooncookie.character.dto.request;

import java.net.URL;

public record AddCharacterRequest(String characterVisionType, Long artworkId, String name, URL selectionThumbnailUrl, String basePrompt) {
}
