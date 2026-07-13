package br.com.csm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "csm")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 100, unique = true)
    private String login;

    @Column(length = 150, unique = true)
    private String email;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(nullable = false)
    private Integer failedAttempts = 0;

    private OffsetDateTime blockedUntil;

    @Column(length = 128)
    private String mfaSecret;

    @Column(nullable = false)
    private Boolean forcePasswordChange = false;

    @Column(length = 64, unique = true)
    private String objectguid;

    private Integer registrationNumber;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(nullable = false)
    private Boolean hiddenTutorial = true;

    private Integer unitId;
    private Integer contractId;
    private Integer photoId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;

    private OffsetDateTime lastLogin;
}
