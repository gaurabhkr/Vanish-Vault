package com.example.onetimesecret.service;

import com.example.onetimesecret.model.Secret;
import com.example.onetimesecret.repository.SecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecretService {

    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public UUID createSecret(String plainText) throws Exception {
        // Generate IV
        byte[] iv = encryptionService.generateIV();
        
        // Encrypt content
        byte[] encryptedContent = encryptionService.encrypt(plainText, iv);
        
        // Create and save secret
        Secret secret = new Secret();
        secret.setEncryptedContent(encryptedContent);
        secret.setIv(iv);
        
        Secret savedSecret = secretRepository.save(secret);
        log.info("Created secret with ID: {}", savedSecret.getId());
        
        return savedSecret.getId();
    }

    @Transactional
    public Optional<String> getSecret(UUID id) throws Exception {
        Optional<Secret> secretOpt = secretRepository.findById(id);
        
        if (secretOpt.isEmpty()) {
            log.warn("Secret not found or already burned: {}", id);
            return Optional.empty();
        }
        
        Secret secret = secretOpt.get();
        
        // Decrypt content
        String decryptedContent = encryptionService.decrypt(
            secret.getEncryptedContent(), 
            secret.getIv()
        );
        
        // Delete the secret immediately (self-destruct)
        secretRepository.delete(secret);
        log.info("Secret burned (deleted): {}", id);
        
        return Optional.of(decryptedContent);
    }
}
