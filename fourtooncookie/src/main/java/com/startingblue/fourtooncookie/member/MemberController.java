package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.dto.MemberSaveRequest;
import com.startingblue.fourtooncookie.member.dto.MemberSavedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<HttpStatus> postMember(UUID memberId, @RequestBody MemberSaveRequest memberSaveRequest) {
        memberService.addMember(memberId, memberSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/member")
    public ResponseEntity<MemberSavedResponse> getMember(UUID memberId) {
        MemberSavedResponse response = MemberSavedResponse.of(memberService.getById(memberId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/member")
    public ResponseEntity<HttpStatus> deleteMember(UUID memberId) {
        memberService.removeById(memberId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
