package com.example.onetimesecret.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_SIZE = 256;

    private final SecretKey secretKey;

    public EncryptionService() throws Exception {
        String envKey = System.getenv("ENCRYPTION_KEY");
        
        if (envKey != null && !envKey.isBlank()) {
            // Use provided key from environment
            byte[] decodedKey = Base64.getDecoder().decode(envKey);
            this.secretKey = new SecretKeySpec(decodedKey, "AES");
            log.info("Encryption service initialized with PERSISTENT key from environment");
        } else {
            // Generate a random AES key for the application (Fallback)
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_SIZE);
            this.secretKey = keyGenerator.generateKey();
            
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            log.warn("Encryption service initialized with RANDOM key. Secrets will be lost on restart!");
            log.info("To make keys persistent, set ENCRYPTION_KEY={}", base64Key);
        }
    }

    public byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public byte[] encrypt(String plainText, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] encryptedContent, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedContent);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
