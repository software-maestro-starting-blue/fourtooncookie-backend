package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.dto.MemberReadDto;
import com.startingblue.fourtooncookie.member.dto.MemberUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;


    public Member findById(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public MemberReadDto readById(final UUID memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        
        return new MemberReadDto(member.getId(), member.getEmail(), member.getName(), member.getBirth(), member.getGender());
    }

    public void updateById(final UUID memberId, final MemberUpdateDto memberUpdateDto) {
        //TODO: 구현 필요
    }

    public void deleteById(final UUID memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        memberRepository.delete(member);
    }
}
