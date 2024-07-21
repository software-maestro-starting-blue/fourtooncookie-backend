package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.character.exception.ModelTypeNoSuchElementException;

public enum ModelType {

    DALL_E_3,
    STABLE_DIFFUSION,
    MIDJOURNEY;

    public static ModelType from(final String value){
        for (final ModelType modelType : ModelType.values()) {
            if (modelType.name().equalsIgnoreCase(value)) {
                return modelType;
            }
        }
        throw new ModelTypeNoSuchElementException();
    }
}
