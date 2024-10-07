package com.startingblue.fourtooncookie.fcm.controller;

import com.startingblue.fourtooncookie.fcm.dto.request.FcmTokenRequest;
import com.startingblue.fourtooncookie.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmTokenService;

    @PostMapping
    public ResponseEntity<?> createFcmToken(@RequestBody FcmTokenRequest request, UUID memberId) {
        fcmTokenService.saveFcmToken(request.token(), memberId);
        return ResponseEntity.ok("Token registered successfully");
    }
}
