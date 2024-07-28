package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.dto.MemberReadDto;
import com.startingblue.fourtooncookie.member.dto.MemberUpdateDto;
import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberReadDto> readMember(@PathVariable final UUID memberId) {
        MemberReadDto memberReadDto = memberService.readById(memberId);
        return ResponseEntity.ok(memberReadDto);
    }

    @PutMapping("/member/{memberId}")
    public ResponseEntity<Void> updateMember(@PathVariable final UUID memberId, final MemberUpdateDto memberUpdateDto) {
        memberService.updateById(memberId, memberUpdateDto);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable final UUID memberId) {
        memberService.deleteById(memberId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
