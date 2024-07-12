package com.startingblue.fourtooncookie;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public DiaryHashtag(Diary diary, Hashtag hashtag) {
        this.diary = diary;
        this.hashtag = hashtag;
    }

    public void assignDiary(Diary diary) {
        if (this.diary != null) {
            this.diary.getHashtags().remove(this);
        }
        this.diary = diary;
        if (diary != null) {
            diary.getHashtags().add(this);
        }
    }
}
