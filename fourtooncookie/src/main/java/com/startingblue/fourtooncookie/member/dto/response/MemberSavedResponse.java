package com.startingblue.fourtooncookie.member.dto.response;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;

import java.time.LocalDate;

public record MemberSavedResponse(String email, String name, Gender gender, LocalDate birth) {

    public static MemberSavedResponse of(Member member) {
        return new MemberSavedResponse(member.getEmail(), member.getName(), member.getGender(), member.getBirth());
    }
}
