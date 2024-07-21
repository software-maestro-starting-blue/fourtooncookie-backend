package com.startingblue.fourtooncookie.character.dto.request;

import java.net.URL;

public record AddCharacterRequest(String modelType, String name, URL selectionThumbnailUrl) {
}
