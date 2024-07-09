package com.startingblue.fourtooncookie.hashtag.exception.weather;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNotFoundException;

public class WeatherHashtagNotFoundException extends HashtagNotFoundException {

    public WeatherHashtagNotFoundException() {
        super("weather hashtag Not Found");
    }

    public WeatherHashtagNotFoundException(String message) {
        super(message);
    }

    public WeatherHashtagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
