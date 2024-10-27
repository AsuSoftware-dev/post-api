package com.asusoftware.post_api.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateImagesRequest {
    private UUID ownerId;
    private ImageType type;
    private List<ImageDto> existingImages;
}
