package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.MemberSavedResponse;
import com.startingblue.fourtooncookie.member.dto.MemberUpdateRequest;
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

    public MemberSavedResponse readById(final UUID memberId) {
        final Member member = findMemberOrThrow(memberId);
        return new MemberSavedResponse(member.getId(), member.getEmail(), member.getName(), member.getBirth(), member.getGender());
    }

    public void updateById(final UUID memberId, final MemberUpdateRequest memberUpdateRequest) {
        Member member = findMemberOrThrow(memberId);
        member.update(memberUpdateRequest.name(), memberUpdateRequest.birth(), memberUpdateRequest.gender());
        memberRepository.save(member);
    }

    public void softDeleteById(UUID memberId, LocalDateTime current) {
        Member foundMember = findMemberOrThrow(memberId);
        foundMember.softDelete(current);
        memberRepository.save(foundMember);
    }

    public Member findMemberOrThrow(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member with ID " + memberId + " not found."));
    }
}
