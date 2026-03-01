package com.example.Service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Entity.Image;
import com.example.Exception.ResourceNotFoundException;
import com.example.Repository.ImageRepository;
import com.example.Service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(Cloudinary cloudinary, ImageRepository imageRepository) {
        this.cloudinary = cloudinary;
        this.imageRepository = imageRepository;
    }

    @Override
    public Image uploadImage(MultipartFile file) throws IOException {
        // Upload image to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Extract required data from result
        String url = uploadResult.get("url").toString();
        String publicId = uploadResult.get("public_id").toString();
        int width = (int) uploadResult.get("width");
        int height = (int) uploadResult.get("height");
        int size = (int) uploadResult.get("bytes");

        // Save image info to DB
        Image image = new Image();
        image.setUrl(url);
        image.setPublicId(publicId);
        image.setWidth(width);
        image.setHeight(height);
        image.setSize(size);
        image.setAlt_text(file.getOriginalFilename());

        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(UUID id) throws IOException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));

        // Delete from Cloudinary
        cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());

        // Delete from DB
        imageRepository.delete(image);
    }

    @Override
    public Image getImageById(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }
}
