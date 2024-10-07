package com.startingblue.fourtooncookie.fcm.domain;

import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fcmToken;

    @OneToOne(mappedBy = "fcmToken")
    private Member member;

    public FcmToken(String fcmToken, Member member) {
        this.fcmToken = fcmToken;
        this.member = member;
    }
}
