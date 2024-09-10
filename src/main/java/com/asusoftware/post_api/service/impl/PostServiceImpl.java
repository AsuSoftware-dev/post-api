package com.asusoftware.post_api.service.impl;

import com.asusoftware.post_api.client.ImageServiceClient;
import com.asusoftware.post_api.client.dto.ImageDto;
import com.asusoftware.post_api.exception.ResourceNotFoundException;
import com.asusoftware.post_api.model.Post;
import com.asusoftware.post_api.model.dto.CreatePostDto;
import com.asusoftware.post_api.model.dto.PostDto;
import com.asusoftware.post_api.model.dto.UpdatePostDto;
import com.asusoftware.post_api.repository.PostRepository;
import com.asusoftware.post_api.service.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageServiceClient imageServiceClient;  // Clientul pentru microserviciul de imagini

    @Transactional
    @Override
    public PostDto createPost(CreatePostDto postDto, List<MultipartFile> images) {
        // Convert DTO to entity
        Post post = CreatePostDto.fromDto(postDto);
        post.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

        // Save the post without images to get the ID
        Post savedPost = postRepository.save(post);

        // If images are provided, upload them using the Image service and get their URLs
        if (images != null && !images.isEmpty()) {
            List<ImageDto> uploadedImages = imageServiceClient.uploadImages(images, savedPost.getId());
            savedPost.setImageIds(uploadedImages.stream().map(ImageDto::getId).collect(Collectors.toList()));
        }

        // Save the post with the image IDs
        Post updatedPost = postRepository.save(savedPost);
        // TODO: add also the images in this dto
        return PostDto.fromEntity(updatedPost);
    }

    @Transactional
    @Override
    public PostDto updatePost(UUID postId, UpdatePostDto postDto, List<MultipartFile> newImages) {
        // Fetch the existing post
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));

        // Update the post fields
        existingPost.setDescription(postDto.getDescription());
        existingPost.setLocationId(postDto.getLocationId());  // Update location if changed
        existingPost.setSubscriptionType(postDto.getSubscriptionType());
       // TODO: de vazut
        //  existingPost.setVisibleToVip(postDto.isVisibleToVip());

        // If new images are provided, upload them and update the post
        if (newImages != null && !newImages.isEmpty()) {
            // Upload the new images and get the IDs
            List<ImageDto> uploadedImages = imageServiceClient.uploadImages(newImages, existingPost.getId());
            List<UUID> newImageIds = uploadedImages.stream().map(ImageDto::getId).collect(Collectors.toList());

            // Replace the existing image IDs with the new ones
            existingPost.setImageIds(newImageIds);
        }

        // Save the updated post
        Post savedPost = postRepository.save(existingPost);

        return PostDto.fromEntity(savedPost);
    }

    @Override
    public PostDto getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return PostDto.fromEntity(post);
    }

    @Override
    public Page<PostDto> getAllPosts(Pageable pageable) {
        // Retrieve the posts with pagination
        Page<Post> postPage = postRepository.findAll(pageable);

        // Map the posts to PostDto and include images
        Page<PostDto> postDtoPage = postPage.map(post -> {
            // Convert Post entity to PostDto
            PostDto postDto = PostDto.fromEntity(post);

            // Fetch images for the post
            List<UUID> imageIds = post.getImageIds();  // Assuming post has a list of image UUIDs
            if (imageIds != null && !imageIds.isEmpty()) {
                // Fetch images for the post
                List<ImageDto> images = imageServiceClient.getImagesByIds(imageIds);
                postDto.setImages(images);
            } else {
                postDto.setImages(Collections.emptyList());  // No images for the post
            }

            return postDto;
        });

        return postDtoPage;
    }

    @Transactional
    @Override
    public void deletePost(UUID postId) {
        // Găsim postarea
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Ștergem imaginile asociate postării
        if (!post.getImageIds().isEmpty()) {
            imageServiceClient.deleteImagesByPostId(post.getImageIds());  // Transmitem ID-urile imaginilor
        }

        // Ștergem postarea din baza de date
        postRepository.delete(post);
    }
}
