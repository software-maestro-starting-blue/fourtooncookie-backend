package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
    }

    @DisplayName("멤버 정보를 업데이트한다.")
    @Test
    void updateMemberInfo() {
        // given
        String updatedName = "Updated Name";
        LocalDate updatedBirth = LocalDate.of(1995, 5, 15);
        Gender updatedGender = Gender.FEMALE;

        // when
        member.update(updatedName, updatedBirth, updatedGender);

        // then
        assertThat(member.getName()).isEqualTo(updatedName);
        assertThat(member.getBirth()).isEqualTo(updatedBirth);
        assertThat(member.getGender()).isEqualTo(updatedGender);
    }

    @DisplayName("멤버가 관리자인지 확인한다.")
    @Test
    void checkIfMemberIsAdmin() {
        // given
        Member adminMember = Member.builder()
                .id(UUID.randomUUID())
                .name("Admin User")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .role(Role.ADMIN)
                .build();

        // when & then
        assertThat(member.isAdmin()).isFalse();
        assertThat(adminMember.isAdmin()).isTrue();
    }
}
