package com.aryan.resumeai.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleTokenVerifierService {

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleIdToken.Payload verifyToken(
            String idTokenString
    ) {

        try {

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier
                            .Builder(
                                    new NetHttpTransport(),
                                    GsonFactory.getDefaultInstance()
                            )
                            .setAudience(
                                    Collections.singletonList(
                                            googleClientId
                                    )
                            )
                            .build();

            GoogleIdToken idToken =
                    verifier.verify(
                            idTokenString
                    );

            if (idToken == null) {

                throw new RuntimeException(
                        "Invalid Google token"
                );
            }

            return idToken.getPayload();

        }  catch (Exception ex) {
            // Add these lines to print the real error in your Spring Boot console
            ex.printStackTrace(); 
            System.err.println("Actual Google API Error: " + ex.getMessage());
            
            throw new RuntimeException(
                    "Google token verification failed: " + ex.getMessage()
            );
        }
    }
}