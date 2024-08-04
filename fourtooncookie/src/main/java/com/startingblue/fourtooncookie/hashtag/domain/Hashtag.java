package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.HashtagDuplicateException;
import com.startingblue.fourtooncookie.hashtag.exception.HashtagNotFoundException;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public enum Hashtag {

    // Weather
    CLEAR(1L, "맑음", HashtagType.WEATHER),
    CLOUDY(2L, "흐림", HashtagType.WEATHER),
    RAIN(3L, "비", HashtagType.WEATHER),
    SNOW(4L, "눈", HashtagType.WEATHER),
    WINDY(5L, "바람", HashtagType.WEATHER),
    YELLOW_DUST(6L, "황사", HashtagType.WEATHER),
    HEATWAVE(7L, "폭염", HashtagType.WEATHER),
    COLD_WAVE(8L, "한파", HashtagType.WEATHER),
    TYPHOON(9L, "태풍", HashtagType.WEATHER),

    // Emotions
    EXCITED(100L, "신나는", HashtagType.EMOTION),
    RELAXED(101L, "편안한", HashtagType.EMOTION),
    PROUD(102L, "뿌듯한", HashtagType.EMOTION),
    ANTICIPATING(103L, "기대되는", HashtagType.EMOTION),
    HAPPY(104L, "행복한", HashtagType.EMOTION),
    MOTIVATED(105L, "의욕적인", HashtagType.EMOTION),
    THRILLED(106L, "설레는", HashtagType.EMOTION),
    REFRESHING(107L, "상쾌한", HashtagType.EMOTION),
    DEPRESSED(108L, "우울한", HashtagType.EMOTION),
    LONELY(109L, "외로운", HashtagType.EMOTION),
    ANXIOUS(110L, "불안한", HashtagType.EMOTION),
    SAD(111L, "슬픈", HashtagType.EMOTION),
    ANGRY(112L, "화난", HashtagType.EMOTION),
    PRESSURED(113L, "부담되는", HashtagType.EMOTION),
    ANNOYED(114L, "짜증나는", HashtagType.EMOTION),
    TIRED(115L, "피곤한", HashtagType.EMOTION),

    // Met Person
    FRIEND(200L, "친구", HashtagType.MET_PERSON),
    FAMILY(201L, "가족", HashtagType.MET_PERSON),
    LOVER(202L, "애인", HashtagType.MET_PERSON),
    ACQUAINTANCE(203L, "지인", HashtagType.MET_PERSON),
    NO_ONE(204L, "안만남", HashtagType.MET_PERSON);

    private static final long WEATHER_MIN_ID = 1L;
    private static final long WEATHER_MAX_ID = 99L;
    private static final long EMOTION_MIN_ID = 100L;
    private static final long EMOTION_MAX_ID = 199L;
    private static final long MET_PERSON_MIN_ID = 200L;
    private static final long MET_PERSON_MAX_ID = 299L;

    private final Long id;
    private final String name;
    private final HashtagType hashtagType;

    Hashtag(Long id, String name, HashtagType hashtagType) {
        hashtagType.validateHashtagId(id);
        this.id = id;
        this.name = name;
        this.hashtagType = hashtagType;
    }

    public static Set<Hashtag> findHashtagsByIds(List<Long> ids) {
        Set<Hashtag> result = EnumSet.noneOf(Hashtag.class);
        for (Long id : ids) {
            Hashtag hashtag = findHashtagById(id);
            result.add(hashtag);
        }
        return result;
    }

    public static Hashtag findHashtagById(Long id) {
        for (Hashtag hashtag : Hashtag.values()) {
            if (hashtag.getId().equals(id)) {
                return hashtag;
            }
        }
        throw new HashtagNotFoundException();
    }

    public static void verifyUniqueIds() {
        Set<Long> ids = new HashSet<>();
        for (Hashtag hashtag : Hashtag.values()) {
            if (!ids.add(hashtag.getId())) {
                throw new HashtagDuplicateException("Duplicate ID found: " + hashtag.getId());
            }
        }
    }

    public static void verifyUniqueNameAndType() {
        Set<String> nameAndTypeSet = new HashSet<>();
        for (Hashtag hashtag : Hashtag.values()) {
            String nameAndType = hashtag.getName() + "-" + hashtag.getHashtagType().name();
            if (!nameAndTypeSet.add(nameAndType)) {
                throw new HashtagDuplicateException("Duplicate name and type combination found: " + nameAndType);
            }
        }
    }

    public static void validateAllHashtags() {
        verifyUniqueIds();
        verifyUniqueNameAndType();
    }
}
