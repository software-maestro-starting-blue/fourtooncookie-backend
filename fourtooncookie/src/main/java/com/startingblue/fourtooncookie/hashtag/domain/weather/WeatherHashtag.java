package com.startingblue.fourtooncookie.hashtag.domain.weather;

import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WeatherHashtag extends Hashtag {

    protected WeatherHashtag(String name) {
        super(name, HashtagType.WEATHER);
    }
}
