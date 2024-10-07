package com.startingblue.fourtooncookie.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FcmConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp initializeFirebaseApp() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized successfully");
                return firebaseApp;
            } else {
                log.info("FirebaseApp already initialized");
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Failed to initialize FirebaseApp: {}", e.getMessage());
            throw new RuntimeException("Could not initialize FirebaseApp", e);
        }
    }
}