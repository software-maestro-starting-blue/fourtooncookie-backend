package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.response.MemberSavedResponse;
import com.startingblue.fourtooncookie.member.dto.request.MemberUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        String email = "test@example.com";
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // then
        MemberSavedResponse memberSavedResponse = MemberSavedResponse.of(memberService.readById(memberId));
        assertThat(memberSavedResponse).isNotNull();
        assertThat(memberSavedResponse.email()).isEqualTo(email);
        assertThat(memberSavedResponse.name()).isEqualTo(name);
        assertThat(memberSavedResponse.birth()).isEqualTo(birth);
        assertThat(memberSavedResponse.gender()).isEqualTo(gender);
    }

    @Test
    @DisplayName("supabase에 있는 멤버 정보를 수정한다.")
    void updateById() {
        UUID memberId = UUID.randomUUID();
        String oldEmail = "oldemail@example.com";
        String oldName = "Old Name";
        LocalDate oldBirth = LocalDate.of(1990, 1, 1);
        Gender oldGender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(oldEmail)
                .name(oldName)
                .birth(oldBirth)
                .gender(oldGender)
                .build();

        String newName = "New Name";
        LocalDate newBirth = LocalDate.of(1991, 2, 2);
        Gender newGender = Gender.FEMALE;

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);

        MemberUpdateRequest updateRequest = new MemberUpdateRequest(newName, newBirth, newGender);
        memberService.updateById(memberId, updateRequest);
        Member updatedMember = memberRepository.findById(memberId).orElse(null);

        // then
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getEmail()).isEqualTo(oldEmail); // Email is not updated in the updateById method
        assertThat(updatedMember.getName()).isEqualTo(newName);
        assertThat(updatedMember.getBirth()).isEqualTo(newBirth);
        assertThat(updatedMember.getGender()).isEqualTo(newGender);
    }

    @Test
    @DisplayName("supabase에 저장된 멤버를 소프트 삭제 한다. deleteAt을 갱신 한다.")
    void softDeleteByIdById() {
        UUID memberId = UUID.randomUUID();
        String email = "test@example.com";
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        LocalDateTime current = LocalDateTime.now();
        memberService.softDeleteById(memberId, current);

        Member deletedMember = memberRepository.findById(memberId).orElse(null);
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getDeletedDateTime()).isEqualTo(current);
    }

    @Test
    @DisplayName("현재 시간보다 미래의 날짜로 소프트 삭제를 시도하면 예외가 발생한다.")
    void softDeleteByIdById_withFutureDate() {
        UUID memberId = UUID.randomUUID();
        String email = "test@example.com";
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        LocalDateTime future = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> memberService.softDeleteById(memberId, future))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current time cannot be after current time");
    }

    @Test
    @DisplayName("현재 시간보다 과거의 날짜로 소프트 삭제를 시도하면 성공한다.")
    void softDeleteByIdById_withPastDate() {
        UUID memberId = UUID.randomUUID();
        String email = "test@example.com";
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        LocalDateTime past = LocalDateTime.now().minusDays(1);
        memberService.softDeleteById(memberId, past);

        Member deletedMember = memberRepository.findById(memberId).orElse(null);
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getDeletedDateTime()).isEqualTo(past);
    }

    @Test
    @DisplayName("null 값을 사용하여 소프트 삭제를 시도하면 예외가 발생한다.")
    void softDeleteByIdById_withNull() {
        UUID memberId = UUID.randomUUID();
        String email = "test@example.com";
        String name = "Test User";
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Gender gender = Gender.MALE;

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.softDeleteById(memberId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current time cannot be null");
    }
}