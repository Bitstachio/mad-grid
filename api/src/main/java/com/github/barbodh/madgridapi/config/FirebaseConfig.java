package com.github.barbodh.madgridapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    @Value("${firebase.database.url}")
    private final String databaseUrl;
    @Value("${firebase.key.path}")
    private final String keyPath;

    @Bean
    public FirebaseApp initialize() throws IOException {
        var serviceAccount = new FileInputStream(keyPath);
        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
