package com.startingblue.fourtooncookie.web.interceptor;

import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MemberSignedUpAuthorizationInterceptor extends MemberAuthorizationInterceptor {

    public MemberSignedUpAuthorizationInterceptor(MemberService memberService) {
        super(memberService);
    }

    @Override
    protected boolean isAuthorized(UUID memberId) {
        boolean isAuthorized = memberService.verifyMemberSignUp(memberId);

        if (isAuthorized) {
            log.info("Authorization success: Member with ID [{}] is already signed up.", memberId);
        } else {
            log.warn("Authorization failed: Member with ID [{}] is not signed up.", memberId);
        }

        return isAuthorized;
    }
}
