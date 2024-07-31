package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.Gender;

import java.time.LocalDate;

public record MemberSavedResponse(String email, String name, Gender gender, LocalDate birth) {

    public static MemberSavedResponse of(String email, String name, Gender gender, LocalDate birth) {
        return new MemberSavedResponse(email, name, gender, birth);
    }
}
