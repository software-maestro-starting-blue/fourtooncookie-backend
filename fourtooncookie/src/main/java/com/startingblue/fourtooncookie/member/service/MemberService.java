package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
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

}
