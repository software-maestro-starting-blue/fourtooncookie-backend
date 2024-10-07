package com.startingblue.fourtooncookie.fcm.domain;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long id;

    @OneToMany(mappedBy = "fcmToken", fetch = FetchType.LAZY)
    private Set<Diary> diaries;

    @Column(nullable = false)
    private String fcmToken;
}
