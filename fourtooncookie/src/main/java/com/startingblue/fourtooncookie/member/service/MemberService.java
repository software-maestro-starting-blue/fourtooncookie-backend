package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.response.MemberSavedResponse;
import com.startingblue.fourtooncookie.member.dto.request.MemberUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
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

    public MemberSavedResponse getById(UUID memberId) {
        return MemberSavedResponse.of(findById(memberId));
    }

    public void updateById(final UUID memberId, final MemberUpdateRequest memberUpdateRequest) {
        Member member = findById(memberId);
        member.update(memberUpdateRequest.name(), memberUpdateRequest.birth(), memberUpdateRequest.gender());
        memberRepository.save(member);
    }

    public void softDeleteById(UUID memberId) {
        softDeleteById(memberId, LocalDateTime.now());
    }

    public void softDeleteById(UUID memberId, LocalDateTime current) {
        Member foundMember = findById(memberId);
        foundMember.softDelete(current);
        memberRepository.save(foundMember);
    }

    public Member findById(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("member not found"));
    }

    public boolean verifyMemberExists(UUID memberId) {
        return  memberRepository.existsById(memberId);
    }
}
