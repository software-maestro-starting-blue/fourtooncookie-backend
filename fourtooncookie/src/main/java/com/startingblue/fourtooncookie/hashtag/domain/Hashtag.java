package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.DiaryHashtag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private HashtagType hashtagType;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryHashtag> diaryHashtags = new ArrayList<>();

    public Hashtag(String name, HashtagType hashtagType) {
        this.name = name;
        this.hashtagType = hashtagType;
    }
}
