package com.startingblue.fourtooncookie.member.domain;

import com.startingblue.fourtooncookie.member.dto.response.MemberSavedResponse;
import com.startingblue.fourtooncookie.member.dto.request.MemberUpdateRequest;
import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberSavedResponse> readMember(@PathVariable final UUID memberId) {
        MemberSavedResponse memberSavedResponse = memberService.readById(memberId);
        return ResponseEntity.ok(memberSavedResponse);
    }

    @PatchMapping("/member/{memberId}")
    public ResponseEntity<Void> updateMember(@PathVariable final UUID memberId, final MemberUpdateRequest memberUpdateRequest) {
        memberService.updateById(memberId, memberUpdateRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Void> softDeleteMember(@PathVariable final UUID memberId) {
        LocalDateTime deleteLocalDateTime = LocalDateTime.now();
        memberService.softDeleteById(memberId, deleteLocalDateTime);
        return ResponseEntity
                .noContent()
                .build();
    }
}
