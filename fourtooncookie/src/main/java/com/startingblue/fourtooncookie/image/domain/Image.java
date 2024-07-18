package com.startingblue.fourtooncookie.image.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Image {

    protected String path;
    protected Integer width;
    protected Integer height;

    protected Image(String path, Integer width, Integer height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }
}
