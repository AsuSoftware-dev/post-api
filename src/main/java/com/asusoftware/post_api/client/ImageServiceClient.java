package com.asusoftware.post_api.client;

import com.asusoftware.post_api.client.dto.ImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "image-service", url = "${image.service.url}")
public interface ImageServiceClient {

    @GetMapping("/api/v1/images")
    List<ImageDto> getImagesByIds(@RequestParam List<UUID> imageIds);

    @PostMapping(value = "/api/v1/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<ImageDto> uploadImages(@RequestPart("images") List<MultipartFile> images, @RequestPart("postId") UUID postId);

    @DeleteMapping("/api/v1/images/by-ids")
    void deleteImagesByPostId(@RequestParam List<UUID> imageIds);
}
