package com.startingblue.fourtooncookie.artwork.exception;

import java.util.NoSuchElementException;

public class ArtworkNoSuchElementException extends NoSuchElementException {

    public ArtworkNoSuchElementException() {
        super("존재하지 않는 작품입니다.");
    }
}
