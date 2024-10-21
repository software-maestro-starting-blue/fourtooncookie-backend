package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.Role;
import com.startingblue.fourtooncookie.member.dto.MemberSaveRequest;
import com.startingblue.fourtooncookie.member.exception.MemberDuplicateException;
import com.startingblue.fourtooncookie.member.exception.MemberNotFoundException;
import com.startingblue.fourtooncookie.member.service.MemberDiaryService;
import com.startingblue.fourtooncookie.notification.NotificationService;
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
    private final NotificationService notificationService;

    public void addMember(UUID memberId, MemberSaveRequest memberSaveRequest) {
        validateMemberExists(memberId);

        Member member = Member.builder()
                .id(memberId)
                .name(memberSaveRequest.name())
                .birth(memberSaveRequest.birth())
                .gender(memberSaveRequest.gender())
                .role(Role.MEMBER)
                .build();

        memberRepository.save(member);
    }

    public Member getById(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("member not found"));
    }

    public void removeById(UUID memberId) {
        memberDiaryService.removeDiariesByMemberId(memberId);
        notificationService.removeAllNotificationTokenFromMember(memberId);
        memberRepository.deleteById(memberId);
    }

    public void validateMemberExists(UUID memberId) {
        if (memberRepository.existsById(memberId)) {
            throw new MemberDuplicateException("Member with id " + memberId + " already exists");
        }
    }

    public boolean isMemberSignUp(UUID memberId) {
        Member member = getById(memberId);
        return member != null && member.getName() != null && !member.getName().isEmpty();
    }

    public boolean isMemberAdmin(UUID memberId) {
        return getById(memberId).isAdmin();
    }
}
