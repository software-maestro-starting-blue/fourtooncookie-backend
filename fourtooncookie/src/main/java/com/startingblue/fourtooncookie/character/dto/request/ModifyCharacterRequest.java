package com.startingblue.fourtooncookie.character.dto.request;

import java.net.URL;

public record ModifyCharacterRequest(String modelType, String name, URL selectionThumbnailUrl) {
}
