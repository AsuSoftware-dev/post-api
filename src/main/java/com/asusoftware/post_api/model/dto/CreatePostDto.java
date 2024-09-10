package com.asusoftware.post_api.model.dto;

import com.asusoftware.post_api.model.Post;
import com.asusoftware.post_api.model.SubscriptionType;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
public class CreatePostDto {

    private UUID userId; // ID of the user creating the post
    private String description; // Content of the post
    private UUID locationId; // Location from the Location Microservice
    private SubscriptionType subscriptionType; // Subscription type (e.g., FREE, VIP)


    public static Post fromDto(CreatePostDto createPostDto) {
        Post post = new Post();
        post.setUserId(createPostDto.getUserId());
        post.setDescription(createPostDto.getDescription());
        post.setLocationId(createPostDto.getLocationId());
        post.setSubscriptionType(createPostDto.getSubscriptionType());
        return post;
    }
}
