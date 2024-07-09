package com.startingblue.fourtooncookie.hashtag.exception.weather;


import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;

public class WeatherHashtagNoSuchElementException extends HashtagNoSuchElementException {

    public WeatherHashtagNoSuchElementException() {
        super("weather hashtag Not Found");
    }

    public WeatherHashtagNoSuchElementException(String message) {
        super(message);
    }

    public WeatherHashtagNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
