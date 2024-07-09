package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private HashtagType hashtagType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    protected Hashtag(String name, HashtagType hashtagType) {
        this.name = name;
        this.hashtagType = hashtagType;
    }

    public void assignToDiary(Diary diary) {
        this.diary = diary;
    }
}
