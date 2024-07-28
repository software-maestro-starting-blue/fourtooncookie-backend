package com.startingblue.fourtooncookie.character.dto.request;

import java.net.URL;

public record ModifyCharacterRequest(String modelType, Long artworkId, String name, URL selectionThumbnailUrl, String basePrompt) {
}
