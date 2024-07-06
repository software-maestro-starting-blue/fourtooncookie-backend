package com.startingblue.fourtooncookie.oauth.domain;

import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth {

    @Id
    @GeneratedValue
    @Column(name = "oauth_id")
    private Long id;

    private String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private OAuthType type;

}
