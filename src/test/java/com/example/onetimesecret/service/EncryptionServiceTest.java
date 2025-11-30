package com.example.onetimesecret.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void testEncryptionAndDecryption() throws Exception {
        // Given
        String originalText = "This is a secret message!";
        byte[] iv = encryptionService.generateIV();

        // When
        byte[] encrypted = encryptionService.encrypt(originalText, iv);
        String decrypted = encryptionService.decrypt(encrypted, iv);

        // Then
        assertEquals(originalText, decrypted);
        assertNotNull(encrypted);
        assertTrue(encrypted.length > 0);
    }

    @Test
    void testIVGeneration() {
        // When
        byte[] iv1 = encryptionService.generateIV();
        byte[] iv2 = encryptionService.generateIV();

        // Then
        assertNotNull(iv1);
        assertNotNull(iv2);
        assertEquals(12, iv1.length); // GCM IV length
        assertFalse(java.util.Arrays.equals(iv1, iv2)); // Should be random
    }

    @Test
    void testEncryptionWithDifferentIVs() throws Exception {
        // Given
        String originalText = "Secret";
        byte[] iv1 = encryptionService.generateIV();
        byte[] iv2 = encryptionService.generateIV();

        // When
        byte[] encrypted1 = encryptionService.encrypt(originalText, iv1);
        byte[] encrypted2 = encryptionService.encrypt(originalText, iv2);

        // Then
        assertFalse(java.util.Arrays.equals(encrypted1, encrypted2));
    }
}
