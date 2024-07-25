package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.character.exception.ModelTypeNoSuchElementException;

public enum CharacterVisionType {

    DALL_E_3,
    STABLE_DIFFUSION,
    MIDJOURNEY;

    public static CharacterVisionType from(final String value){
        for (final CharacterVisionType characterVisionType : CharacterVisionType.values()) {
            if (characterVisionType.name().equalsIgnoreCase(value)) {
                return characterVisionType;
            }
        }
        throw new ModelTypeNoSuchElementException();
    }
}
