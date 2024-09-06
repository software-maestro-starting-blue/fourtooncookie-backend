package com.startingblue.fourtooncookie.member.domain;

import com.startingblue.fourtooncookie.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "deleted_date_time")
    private LocalDateTime deletedDateTime;

    public void update(String name, LocalDate birth, Gender gender) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
    }

    public void softDelete(LocalDateTime current) {
        if (current == null) {
            throw new IllegalArgumentException("Current time cannot be null");
        }
        if (current.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Current time cannot be after current time");
        }
        if (deletedDateTime != null && current.isAfter(deletedDateTime)) {
            throw new IllegalArgumentException("Cannot delete at a time after the current deletedAt timestamp");
        }
        deletedDateTime = current;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
