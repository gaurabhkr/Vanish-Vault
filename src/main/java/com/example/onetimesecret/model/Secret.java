package com.example.onetimesecret.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "secrets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] encryptedContent;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] iv;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
