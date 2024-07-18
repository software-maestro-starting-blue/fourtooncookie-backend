package com.startingblue.fourtooncookie.member;

import com.startingblue.fourtooncookie.member.dto.MemberDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @GetMapping
    public String getMember(MemberDto memberDto) {
        return "Member ID: " + memberDto.memberId();
    }
}
