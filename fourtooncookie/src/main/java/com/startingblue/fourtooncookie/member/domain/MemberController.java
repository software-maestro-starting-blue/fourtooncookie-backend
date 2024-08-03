package com.startingblue.fourtooncookie.member.domain;

import com.startingblue.fourtooncookie.member.dto.response.MemberSavedResponse;
import com.startingblue.fourtooncookie.member.dto.request.MemberUpdateRequest;
import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberSavedResponse> readMember(@PathVariable final UUID memberId) {
        MemberSavedResponse response = MemberSavedResponse.of(memberService.readById(memberId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/member/{memberId}")
    public ResponseEntity<HttpStatus> updateMember(@PathVariable final UUID memberId, final MemberUpdateRequest memberUpdateRequest) {
        memberService.updateById(memberId, memberUpdateRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<HttpStatus> softDeleteMember(@PathVariable final UUID memberId) {
        memberService.softDeleteById(memberId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
