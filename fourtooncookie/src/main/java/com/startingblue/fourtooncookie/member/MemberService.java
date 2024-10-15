package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.diary.MemberDiaryService;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.Role;
import com.startingblue.fourtooncookie.member.dto.MemberSaveRequest;
import com.startingblue.fourtooncookie.member.exception.MemberDuplicateException;
import com.startingblue.fourtooncookie.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberDiaryService memberDiaryService;

    public void save(UUID memberId, MemberSaveRequest memberSaveRequest) {
        if (verifyMemberExists(memberId)) {
            throw new MemberDuplicateException("Member with id " + memberId + " already exists");
        }

        Member member = Member.builder()
                .id(memberId)
                .name(memberSaveRequest.name())
                .birth(memberSaveRequest.birth())
                .genderEnum(memberSaveRequest.genderEnum())
                .role(Role.MEMBER)
                .build();
        memberRepository.save(member);
    }

    public Member readById(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("member not found"));
    }
    // 히스토리 -> 배치
    public void hardDeleteById(UUID memberId) {
        memberDiaryService.deleteDiariesByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
    // is, exists
    public boolean verifyMemberExists(UUID memberId) {
        return memberRepository.existsById(memberId);
    }

    // 없 앨 코드
    public boolean verifyMemberSignUp(UUID memberId) {
        Member member = readById(memberId);
        return member != null && member.getName() != null && !member.getName().isEmpty();
    }

    // 어드민 따로 구분(컨트롤러, 토큰 등)
    public boolean verifyMemberAdmin(UUID memberId) {
        Member member = readById(memberId);
        return member.isAdmin();
    }
}
