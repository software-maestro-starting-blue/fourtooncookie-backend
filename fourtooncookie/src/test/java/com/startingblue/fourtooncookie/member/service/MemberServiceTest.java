package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.domain.Role;
import com.startingblue.fourtooncookie.member.dto.request.MemberSaveRequest;
import com.startingblue.fourtooncookie.member.exception.MemberDuplicateException;
import com.startingblue.fourtooncookie.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberDiaryService memberDiaryService;

    private UUID memberId;
    private MemberSaveRequest memberSaveRequest;
    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        memberSaveRequest = new MemberSaveRequest("Test User", LocalDate.of(2000, 1, 1), Gender.MALE);

        member = Member.builder()
                .id(memberId)
                .name("Test User")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
    }

    @Test
    @DisplayName("멤버 저장 시 중복 멤버가 있는 경우 MemberDuplicateException 발생")
    void save_ThrowsException_WhenMemberExists() {
        // given
        when(memberRepository.existsById(memberId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.save(memberId, memberSaveRequest))
                .isInstanceOf(MemberDuplicateException.class)
                .hasMessageContaining("Member with id " + memberId + " already exists");

        verify(memberRepository, never()).save(any(Member.class)); // 멤버 저장 호출이 발생하지 않음
    }

    @Test
    @DisplayName("새로운 멤버를 성공적으로 저장")
    void save_Success_WhenMemberDoesNotExist() {
        // given
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // when
        memberService.save(memberId, memberSaveRequest);

        // then
        verify(memberRepository, times(1)).save(any(Member.class)); // 한 번 호출
    }

    @Test
    @DisplayName("멤버 ID로 조회 시 멤버를 찾는다.")
    void readById_ReturnsMember_WhenMemberExists() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        Member foundMember = memberService.readById(memberId);

        // then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getName()).isEqualTo("Test User");
        assertThat(foundMember.getBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("멤버 ID로 조회 시 멤버를 찾을 수 없을 때 MemberNotFoundException 발생")
    void readById_ThrowsException_WhenMemberNotFound() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.readById(memberId))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("member not found");
    }

    @Test
    @DisplayName("멤버를 성공적으로 삭제")
    void hardDeleteById_DeletesMember_WhenCalled() {
        doNothing().when(memberDiaryService).deleteDiariesByMemberId(memberId);

        // when
        memberService.hardDeleteById(memberId);

        // then
        verify(memberRepository, times(1)).deleteById(memberId); // 삭제 호출
    }

    @Test
    @DisplayName("멤버 존재 여부를 확인")
    void verifyMemberExists_ReturnsTrue_WhenMemberExists() {
        // given
        when(memberRepository.existsById(memberId)).thenReturn(true);

        // when
        boolean exists = memberService.verifyMemberExists(memberId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("멤버가 존재하지 않으면 false 반환")
    void verifyMemberExists_ReturnsFalse_WhenMemberNotExists() {
        // given
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // when
        boolean exists = memberService.verifyMemberExists(memberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("멤버의 가입 여부 확인")
    void verifyMemberSignUp_ReturnsTrue_WhenSignedUp() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        boolean signedUp = memberService.verifyMemberSignUp(memberId);

        // then
        assertThat(signedUp).isTrue();
    }

    @Test
    @DisplayName("멤버의 가입 상태가 불완전할 경우 false 반환")
    void verifyMemberSignUp_ReturnsFalse_WhenNotSignedUp() {
        // given
        Member incompleteMember = Member.builder().id(memberId).build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(incompleteMember));

        // when
        boolean signedUp = memberService.verifyMemberSignUp(memberId);

        // then
        assertThat(signedUp).isFalse();
    }

    @Test
    @DisplayName("멤버가 관리자인 경우 true 반환")
    void verifyMemberAdmin_ReturnsTrue_WhenAdmin() {
        // given
        UUID adminMemberId = UUID.randomUUID();
        Member adminMember = Member.builder()
                .id(adminMemberId)
                .name("Test User")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .role(Role.ADMIN)
                .build();
        when(memberRepository.findById(adminMemberId)).thenReturn(Optional.of(adminMember));

        // when
        boolean isAdmin = memberService.verifyMemberAdmin(adminMemberId);

        // then
        assertThat(isAdmin).isTrue();
    }

    @Test
    @DisplayName("멤버가 관리자가 아닐 경우 false 반환")
    void verifyMemberAdmin_ReturnsFalse_WhenNotAdmin() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        boolean isAdmin = memberService.verifyMemberAdmin(memberId);

        // then
        assertThat(isAdmin).isFalse();
    }
}
