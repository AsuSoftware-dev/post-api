package com.asusoftware.post_api.controller;

import com.asusoftware.post_api.exception.*;
import com.asusoftware.post_api.model.dto.CreatePostDto;
import com.asusoftware.post_api.model.dto.PostDto;
import com.asusoftware.post_api.model.dto.UpdatePostDto;
import com.asusoftware.post_api.service.PostService;
import com.asusoftware.post_api.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(
            @RequestPart("post") CreatePostDto postDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        PostDto createdPost = postService.createPost(postDto, images);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID postId,
            @RequestPart("postDto") UpdatePostDto postDto,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {

        PostDto updatedPost = postService.updatePost(postId, postDto, newImages);
        return ResponseEntity.ok(updatedPost);
    }

    // Like a post
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable UUID postId, @RequestParam UUID userId) {
        try {
        //    postService.likePost(postId, userId);
            return new ResponseEntity<>("Post liked successfully", HttpStatus.OK);
        } catch (LikeOperationException e) {
            throw new LikeOperationException("Failed to like post: " + e.getMessage());
        }
    }

    // Comment on a post
    @PostMapping("/{postId}/comment")
    public ResponseEntity<String> commentOnPost(@PathVariable UUID postId, @RequestParam UUID userId, @RequestBody String commentText) {
        try {
         //   postService.commentOnPost(postId, userId, commentText);
            return new ResponseEntity<>("Comment added successfully", HttpStatus.OK);
        } catch (CommentOperationException e) {
            throw new CommentOperationException("Failed to comment on post: " + e.getMessage());
        }
    }

    // Get a post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID postId) {
        try {
            PostDto postDto = postService.getPostById(postId);
            return new ResponseEntity<>(postDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Post not found: " + e.getMessage());
        }
    }

    // Get all posts (with pagination)
    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(Pageable pageable) {
        Page<PostDto> posts = postService.getAllPosts(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }
}