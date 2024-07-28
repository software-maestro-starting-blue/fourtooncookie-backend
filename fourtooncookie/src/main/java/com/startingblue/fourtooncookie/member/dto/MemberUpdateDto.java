package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.Gender;

import java.time.LocalDate;

public record MemberUpdateDto(String name, LocalDate birth, Gender gender) {
}
