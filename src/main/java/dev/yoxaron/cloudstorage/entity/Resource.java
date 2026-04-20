package dev.yoxaron.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(name = "size")
    private Long size;

    @Column(name = "uuid")
    private UUID uuid;
}
