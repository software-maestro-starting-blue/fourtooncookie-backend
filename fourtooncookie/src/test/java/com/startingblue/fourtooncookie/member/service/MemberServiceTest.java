package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.response.MemberSavedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("supabase에 저장된 멤버를 찾는다.")
    void getById() {
        // given
        UUID memberId = UUID.randomUUID();
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // then
        MemberSavedResponse memberSavedResponse = MemberSavedResponse.of(memberService.readById(memberId));
        assertThat(memberSavedResponse).isNotNull();
        assertThat(memberSavedResponse.name()).isEqualTo(name);
        assertThat(memberSavedResponse.birth()).isEqualTo(birth);
        assertThat(memberSavedResponse.gender()).isEqualTo(gender);
    }
}