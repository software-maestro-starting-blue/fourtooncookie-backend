package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
@RequiredArgsConstructor
public class MemberSignUpAuthorizationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_KEY = "memberId";
    private static final Logger log = LoggerFactory.getLogger(MemberSignUpAuthorizationInterceptor.class);
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String memberIdStr = String.valueOf(request.getAttribute(PATH_VARIABLE_KEY));

        if (memberIdStr == null || memberIdStr.isEmpty()) {
            log.warn("Missing or empty path variable '{}'", PATH_VARIABLE_KEY);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        UUID memberId;
        try {
            memberId = UUID.fromString(memberIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for memberId: {}", memberIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        if (isAuthorized(memberId)) {
            log.info("Member with id {} is authorized", memberId);
            return true;
        }

        log.info("Member with id {} is not authorized", memberId);
        response.setStatus(SC_FORBIDDEN);
        return false;
    }

    private boolean isAuthorized(UUID memberId) {
        return memberService.verifyMemberSignUp(memberId);
    }
}
