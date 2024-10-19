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

    @GetMapping("/member")
    public ResponseEntity<MemberSavedResponse> readMember(UUID memberId) {
        MemberSavedResponse response = MemberSavedResponse.of(memberService.readById(memberId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/member")
    public ResponseEntity<HttpStatus> saveMember(UUID memberId, @RequestBody MemberSaveRequest memberSaveRequest) {
        memberService.save(memberId, memberSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/member")
    public ResponseEntity<HttpStatus> hardDeleteMember(UUID memberId) {
        memberService.hardDeleteById(memberId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
