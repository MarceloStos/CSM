package br.com.csm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications", schema = "csm")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80, unique = true)
    private String clientId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String clientSecretHash;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 30)
    private String acronym;

    @Column(length = 255)
    private String url;

    @Column(nullable = false, length = 255)
    private String redirectUri;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(nullable = false)
    private Boolean isPublished = false;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 255)
    private String requester;

    private LocalDate projectStartDate;

    @Column(length = 200)
    private String gitNamespace;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    private OffsetDateTime deactivatedAt;
}
