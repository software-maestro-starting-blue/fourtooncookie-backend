package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@RequiredArgsConstructor
@Slf4j
public abstract class MemberAuthorizationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_KEY = "memberId";
    protected final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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

    protected abstract boolean isAuthorized(UUID memberId);
}
