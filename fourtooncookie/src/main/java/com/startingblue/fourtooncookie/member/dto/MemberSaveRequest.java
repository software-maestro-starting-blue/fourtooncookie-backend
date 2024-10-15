package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.GenderEnum;

import java.time.LocalDate;

public record MemberSaveRequest(String name, LocalDate birth, GenderEnum genderEnum) {
}
