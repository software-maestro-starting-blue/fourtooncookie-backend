package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.request.MemberUpdateRequest;
import com.startingblue.fourtooncookie.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member readById(UUID memberId) {
        return  memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("member not found"));
    }

    public void updateById(final UUID memberId, final MemberUpdateRequest memberUpdateRequest) {
        Member member = readById(memberId);
        member.update(memberUpdateRequest.name(), memberUpdateRequest.birth(), memberUpdateRequest.gender());
        memberRepository.save(member);
    }

    public void softDeleteById(UUID memberId) {
        softDeleteById(memberId, LocalDateTime.now());
    }

    public void softDeleteById(UUID memberId, LocalDateTime current) {
        Member foundMember = readById(memberId);
        foundMember.softDelete(current);
        memberRepository.save(foundMember);
    }

    public boolean verifyMemberExists(UUID memberId) {
        return memberRepository.existsById(memberId);
    }

    public boolean isMemberSignUpAuthorized(UUID memberId) {
        Member member = readById(memberId);
        return member != null && member.getName() != null && !member.getName().isEmpty();
    }

}
