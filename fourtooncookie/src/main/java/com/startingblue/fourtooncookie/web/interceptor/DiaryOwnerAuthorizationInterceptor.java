package com.startingblue.fourtooncookie.web.interceptor;

import com.startingblue.fourtooncookie.diary.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        String memberId = String.valueOf(request.getAttribute(PATH_VARIABLE_MEMBER_KEY));
        validateExistsId(memberId, response);
        
        String diaryId = extractPathVariable(request.getRequestURI());
        validateExistsId(diaryId, response);

        try {
            boolean isAuthorized = isAuthorized(UUID.fromString(memberId), Long.parseLong(diaryId));

            if (! isAuthorized) {
                response.setStatus(SC_FORBIDDEN);
            }

            return isAuthorized;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for memberId: {}", memberId);
            response.setStatus(SC_FORBIDDEN);
            return false;
        }
    }

    private void validateExistsId(String id, HttpServletResponse response) {
        if (!StringUtils.hasText(id)) {
            log.warn("Missing or empty path variable '{}': {}", PATH_VARIABLE_MEMBER_KEY, id);
            response.setStatus(SC_FORBIDDEN);
        }
    }

    private boolean isAuthorized(UUID memberId, long diaryId) {
        return diaryService.isDiaryOwner(memberId, diaryId);
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
