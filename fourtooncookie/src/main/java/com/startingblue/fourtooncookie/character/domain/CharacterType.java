package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.character.exception.ModelTypeNoSuchElementException;

public enum CharacterType {

    DALL_E_3,
    STABLE_DIFFUSION,
    MIDJOURNEY;

    public static CharacterType from(final String value){
        for (final CharacterType characterType : CharacterType.values()) {
            if (characterType.name().equalsIgnoreCase(value)) {
                return characterType;
            }
        }
        throw new ModelTypeNoSuchElementException();
    }
}
