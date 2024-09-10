package com.asusoftware.post_api.repository;

import com.asusoftware.post_api.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    // Find all posts with pagination
    Page<Post> findAll(Pageable pageable);

    // Example to filter posts by userId and paginate the result
    Page<Post> findByUserId(UUID userId, Pageable pageable);
}
