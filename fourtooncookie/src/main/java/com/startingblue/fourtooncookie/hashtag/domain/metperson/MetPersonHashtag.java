package com.startingblue.fourtooncookie.hashtag.domain.metperson;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MetPersonHashtag extends Hashtag {

    protected MetPersonHashtag(String name) {
        super(name, HashtagType.MET_PERSON);
    }
}
