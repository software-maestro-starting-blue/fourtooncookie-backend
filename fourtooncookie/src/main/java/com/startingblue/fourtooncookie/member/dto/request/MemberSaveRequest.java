package com.startingblue.fourtooncookie.member.dto.request;

import com.startingblue.fourtooncookie.member.domain.Gender;

import java.time.LocalDate;

public record MemberSaveRequest(String email, String name, LocalDate birth, Gender gender) {
}
