package com.asusoftware.post_api.service;

import com.asusoftware.post_api.model.dto.CreatePostDto;
import com.asusoftware.post_api.model.dto.PostDto;
import com.asusoftware.post_api.model.dto.UpdatePostDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;

public interface PostService {
    PostDto createPost(CreatePostDto postDto, List<MultipartFile> images);
    PostDto updatePost(UUID postId, UpdatePostDto postDto, List<MultipartFile> images);
    PostDto getPostById(UUID postId);
    Page<PostDto> getAllPosts(Pageable pageable);
    void deletePost(UUID postId);
}
