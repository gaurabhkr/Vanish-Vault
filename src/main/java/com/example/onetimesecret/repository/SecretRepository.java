package com.example.onetimesecret.repository;

import com.example.onetimesecret.model.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SecretRepository extends JpaRepository<Secret, UUID> {
}
