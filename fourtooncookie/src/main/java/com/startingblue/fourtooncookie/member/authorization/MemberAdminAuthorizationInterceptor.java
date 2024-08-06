package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MemberAdminAuthorizationInterceptor extends MemberAuthorizationInterceptor {

    public MemberAdminAuthorizationInterceptor(MemberService memberService) {
        super(memberService);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("GET".equalsIgnoreCase(request.getMethod()) &&
                ( request.getRequestURI().startsWith("/character") ||
                 request.getRequestURI().startsWith("/artwork"))
        ) {
            return true;
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    protected boolean isAuthorized(UUID memberId) {
        boolean isAuthorized = memberService.verifyMemberAdmin(memberId);

        if (isAuthorized) {
            log.info("Authorization success: Member with ID [{}] has admin privileges.", memberId);
        } else {
            log.warn("Authorization failed: Member with ID [{}] does not have admin privileges.", memberId);
        }

        return isAuthorized;
    }
}