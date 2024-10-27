package com.asusoftware.post_api.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ImageDto {

    private UUID id;
    private String fileName;
    private String fileUrl;
}
