package com.startingblue.fourtooncookie.diary.authorization;

import com.startingblue.fourtooncookie.diary.service.DiaryService;
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

        if (memberIdStr == null || memberIdStr.isEmpty()) {
            log.warn("Missing or empty path variable '{}': {}", PATH_VARIABLE_MEMBER_KEY, memberIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        String diaryIdStr = extractPathVariable(request.getRequestURI());
        if (diaryIdStr == null || diaryIdStr.isEmpty()) {
            log.warn("Missing or empty path variable '{}': {}", PATH_VARIABLE_DIARY_KEY, diaryIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        UUID memberId;
        long diaryId;
        try {
            memberId = UUID.fromString(memberIdStr);
            diaryId = Long.parseLong(diaryIdStr);
            System.out.println(diaryId);
            if (isAuthorized(memberId, diaryId)) {
                log.info("Member with id {} is authorized", memberId);
                return true;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid UUID format for diaryId: {}", diaryIdStr);
            response.setStatus(SC_FORBIDDEN);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for memberId: {}", memberIdStr);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        log.warn("Member with id {} is not authorized for diary {}", memberIdStr, diaryIdStr);
        response.setStatus(SC_FORBIDDEN);
        return false;
    }

    private boolean isAuthorized(UUID memberId, Long diaryId) {
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
