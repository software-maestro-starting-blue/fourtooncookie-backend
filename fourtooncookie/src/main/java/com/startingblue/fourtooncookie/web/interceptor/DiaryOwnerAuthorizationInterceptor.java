package com.startingblue.fourtooncookie.web.interceptor;

import com.startingblue.fourtooncookie.diary.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiaryOwnerAuthorizationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_MEMBER_KEY = "memberId";
    private static final String PATH_VARIABLE_DIARY_KEY = "diaryId";

    private final DiaryService diaryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String memberIdStr = String.valueOf(request.getAttribute(PATH_VARIABLE_MEMBER_KEY));
        if (!isValidMemberId(memberIdStr, response)) {
            return false;
        }

        String diaryIdStr = extractPathVariable(request.getRequestURI());
        if (!isValidDiaryId(diaryIdStr, response)) {
            return false;
        }

        try {
            UUID memberId = UUID.fromString(memberIdStr);
            long diaryId = Long.parseLong(diaryIdStr);
            return handleAuthorization(memberId, diaryId, response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for memberId: {}", memberIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }
    }

    private boolean isValidMemberId(String memberIdStr, HttpServletResponse response) {
        if (memberIdStr == null || memberIdStr.isEmpty()) {
            log.warn("Missing or empty path variable '{}': {}", PATH_VARIABLE_MEMBER_KEY, memberIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean isValidDiaryId(String diaryIdStr, HttpServletResponse response) {
        if (diaryIdStr == null || diaryIdStr.isEmpty()) {
            log.warn("Missing or empty path variable '{}': {}", PATH_VARIABLE_DIARY_KEY, diaryIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean handleAuthorization(UUID memberId, long diaryId, HttpServletResponse response) {
        if (isAuthorized(memberId, diaryId)) {
            log.info("Member with id {} is authorized", memberId);
            return true;
        }
        log.warn("Member with id {} is not authorized for diary {}", memberId, diaryId);
        response.setStatus(SC_FORBIDDEN);
        return false;
    }

    private boolean isAuthorized(UUID memberId, long diaryId) {
        return diaryService.verifyDiaryOwner(memberId, diaryId);
    }

    private String extractPathVariable(String requestURI) {
        Pattern pattern = Pattern.compile("/diary/(?<" + PATH_VARIABLE_DIARY_KEY + ">[^/]+)");
        Matcher matcher = pattern.matcher(requestURI);
        if (matcher.find()) {
            return matcher.group(PATH_VARIABLE_DIARY_KEY);
        }
        return null;
    }
}
