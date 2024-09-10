package com.asusoftware.post_api.model.dto;

import com.asusoftware.post_api.client.dto.ImageDto;
import com.asusoftware.post_api.model.Post;
import com.asusoftware.post_api.model.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PostDto {
    private UUID id;
    private String description;
    private LocalDateTime createdAt;
    private UUID userId;  // ID-ul utilizatorului care a creat postarea
    private UUID locationId;  // ID-ul locației din microserviciul Location
    private SubscriptionType subscriptionType;  // Tipul de abonament pentru a restricționa postările (Free/VIP)
    private List<ImageDto> images;  // Lista cu imaginile asociate postării

    public static PostDto fromEntity(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setDescription(post.getDescription());
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setUserId(post.getUserId());
        postDto.setLocationId(post.getLocationId());
        postDto.setSubscriptionType(post.getSubscriptionType());
        return postDto;
    }

    public static Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setId(postDto.getId());
        post.setDescription(postDto.getDescription());
        post.setCreatedAt(postDto.getCreatedAt());
        post.setUserId(postDto.getUserId());
        post.setLocationId(postDto.getLocationId());
        post.setSubscriptionType(postDto.getSubscriptionType());
        return post;
    }
}
