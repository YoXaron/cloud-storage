package dev.yoxaron.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resources")
@Getter
@Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ToString.Include
    private Long userId;

    @Column(nullable = false)
    @ToString.Include
    private String path;

    @Column(nullable = false)
    @ToString.Include
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private ResourceType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceStatus status;

    private Long size;

    @Column(unique = true)
    private UUID uuid;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}
