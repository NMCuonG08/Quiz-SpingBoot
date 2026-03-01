package com.example.Service;

import com.example.Entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.util.List;
import java.io.IOException;

public interface ImageService {
    Image uploadImage(MultipartFile file) throws IOException;

    void deleteImage(UUID id) throws IOException;

    Image getImageById(UUID id);

    List<Image> getAllImages();
}
