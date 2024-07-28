package com.startingblue.fourtooncookie.config.authentication;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AuthenticationExceptionHandler {

    public void handleAuthenticationException(AuthenticationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        response.setStatus(e.getStatusCode());
        response.getWriter().write(e.getMessage());
    }

    public void handleGeneralException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Authentication error", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Internal server error");
    }
}
