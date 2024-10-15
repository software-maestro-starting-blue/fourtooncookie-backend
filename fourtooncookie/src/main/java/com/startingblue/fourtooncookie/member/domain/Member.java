package com.startingblue.fourtooncookie.member.domain;

import com.startingblue.fourtooncookie.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "genderEnum")
    private GenderEnum genderEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "deleted_date_time")
    private LocalDateTime deletedDateTime;

    public void update(String name, LocalDate birth, GenderEnum genderEnum) {
        this.name = name;
        this.birth = birth;
        this.genderEnum = genderEnum;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
