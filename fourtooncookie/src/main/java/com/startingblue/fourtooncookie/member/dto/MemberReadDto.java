package com.startingblue.fourtooncookie.member.dto;

import com.startingblue.fourtooncookie.member.domain.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record MemberReadDto(UUID memberId, String email, String name, LocalDate birth, Gender gender) {
}
