package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.GenderEnum;
import com.startingblue.fourtooncookie.member.domain.Member;

import java.time.LocalDate;

public record MemberSavedResponse(String name, GenderEnum genderEnum, LocalDate birth) {

    public static MemberSavedResponse of(Member member) {
        return new MemberSavedResponse(member.getName(), member.getGenderEnum(), member.getBirth());
    }
}
