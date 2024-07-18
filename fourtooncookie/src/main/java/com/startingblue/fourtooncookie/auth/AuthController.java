package com.startingblue.fourtooncookie.auth;

import com.startingblue.fourtooncookie.jwt.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
            if ("user".equals(loginRequest.username()) && "password".equals(loginRequest.password())) {
                log.info("true");
            String token = JwtProvider.generateToken(1L);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
