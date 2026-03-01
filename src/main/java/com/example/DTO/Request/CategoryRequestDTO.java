package com.example.DTO.Request;

import lombok.Data;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryRequestDTO {
    private String name;
    private String slug;
    private String description;
    private UUID parentId;
    private Integer sortOrder;
    private Boolean isActive;
    private MultipartFile file;
}
