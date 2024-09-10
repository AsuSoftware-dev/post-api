package com.asusoftware.post_api.model.dto;

import com.asusoftware.post_api.model.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePostDto {
    private UUID userId; // ID of the user creating the post
    private String description; // Content of the post
    private UUID locationId; // Location from the Location Microservice
    private SubscriptionType subscriptionType; // Subscription type (e.g., FREE, VIP)
}
