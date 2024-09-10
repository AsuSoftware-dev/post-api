package com.asusoftware.post_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID locationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType subscriptionType;  // FREE sau VIP

    // Lista de ID-uri ale imaginilor stocate în microserviciul Image
    @ElementCollection
    @CollectionTable(name = "post_image_ids", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_id")
    private List<UUID> imageIds = new ArrayList<>();

}
