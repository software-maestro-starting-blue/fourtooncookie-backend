package com.startingblue.fourtooncookie.hashtag.domain.emotion;

import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionHashtag extends Hashtag {

    protected EmotionHashtag(String name) {
        super(name, HashtagType.EMOTION);
    }
}
