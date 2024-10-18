package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;

import java.time.LocalDate;

public record MemberSavedResponse(String name, Gender gender, LocalDate birth) {

    public static MemberSavedResponse of(Member member) {
        return new MemberSavedResponse(member.getName(), member.getGender(), member.getBirth());
    }
}
