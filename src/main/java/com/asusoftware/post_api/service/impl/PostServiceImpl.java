package com.asusoftware.post_api.service.impl;

import com.asusoftware.post_api.client.ImageServiceClient;
import com.asusoftware.post_api.client.dto.ImageDto;
import com.asusoftware.post_api.client.dto.ImageType;
import com.asusoftware.post_api.client.dto.UpdateImagesRequest;
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
import java.util.ArrayList;
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

        PostDto updatedPostDto;

        // If images are provided, upload them using the Image service and get their URLs
        if (images != null && !images.isEmpty()) {
            String ownerId = savedPost.getId().toString();  // Convert UUID to String
            String imageType = ImageType.POST.name();       // Convert enum to String

            List<ImageDto> uploadedImages = imageServiceClient.uploadImages(images, ownerId, imageType);
            savedPost.setImageIds(uploadedImages.stream().map(ImageDto::getId).collect(Collectors.toList()));
            // Save the post with the image IDs
            Post updatedPost = postRepository.save(savedPost);
            // TODO: add also the images in this dto
            updatedPostDto = PostDto.fromEntity(updatedPost);
            updatedPostDto.setImages(uploadedImages);
        } else {
            updatedPostDto = PostDto.fromEntity(savedPost);
        }
        return updatedPostDto;
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
            UpdateImagesRequest updateImagesRequest = new UpdateImagesRequest();
            updateImagesRequest.setType(ImageType.POST);
            updateImagesRequest.setExistingImages(postDto.getExistingImages());
            updateImagesRequest.setOwnerId(postId);
            List<ImageDto> uploadedImages = imageServiceClient.updateImages(updateImagesRequest, newImages);
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
        PostDto postDto = PostDto.fromEntity(post);
        List<ImageDto> imageList;
        if(!post.getImageIds().isEmpty()) {
            imageList = imageServiceClient.getImagesByOwnerId(post.getId(), ImageType.POST);
            postDto.setImages(imageList);
        }
        return postDto;
    }

    @Override
    public Page<PostDto> getAllPosts(Pageable pageable) {
        // Retrieve the posts with pagination
        Page<Post> postPage = postRepository.findAll(pageable);

        // Map the posts to PostDto and include images
        Page<PostDto> postDtoPage = postPage.map(post -> {
            List<ImageDto> imageList;
            // Convert Post entity to PostDto
            PostDto postDto = PostDto.fromEntity(post);
            if(!post.getImageIds().isEmpty()) {
                imageList = imageServiceClient.getImagesByOwnerId(post.getId(), ImageType.POST);
                postDto.setImages(imageList);
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
            imageServiceClient.deleteAllImages(post.getId(), ImageType.POST);  // Transmitem ID-urile imaginilor
        }

        // Ștergem postarea din baza de date
        postRepository.delete(post);
    }
}
