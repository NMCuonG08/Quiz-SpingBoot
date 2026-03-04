package com.example.Service.Impl;

import com.example.DTO.Request.CategoryRequestDTO;
import com.example.DTO.Response.CategoryResponseDTO;
import com.example.Entity.Category;
import com.example.Entity.Image;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.CategoryMapper;
import com.example.Repository.CategoryRepository;
import com.example.Service.CategoryService;
import com.example.Service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;
    private final com.example.Service.RedisService redisService;
    private static final String REDIS_KEY_ALL_CATEGORIES = "CATEGORIES_ALL";

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
            ImageService imageService, com.example.Service.RedisService redisService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.imageService = imageService;
        this.redisService = redisService;
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        MultipartFile file = categoryRequestDTO.getFile();
        Category category = categoryMapper.toEntity(categoryRequestDTO);

        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                category.setIcon_url(image.getUrl());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        Category savedCategory = categoryRepository.save(category);
        redisService.delete(REDIS_KEY_ALL_CATEGORIES); // Invalid cache
        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO categoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryMapper.updateEntityFromRequestDTO(existingCategory, categoryRequestDTO);

        MultipartFile file = categoryRequestDTO.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                existingCategory.setIcon_url(image.getUrl());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        redisService.delete(REDIS_KEY_ALL_CATEGORIES); // Invalid cache
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        existingCategory.setIsDeleted(true); // Assuming soft delete
        categoryRepository.save(existingCategory);
        redisService.delete(REDIS_KEY_ALL_CATEGORIES); // Invalid cache
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return categoryMapper.toResponseDTO(category);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        if (redisService.hasKey(REDIS_KEY_ALL_CATEGORIES)) {
            System.out.println("Fetching from Redis Cache...");
            return (List<CategoryResponseDTO>) redisService.get(REDIS_KEY_ALL_CATEGORIES);
        }

        System.out.println("Fetching from Database...");
        List<CategoryResponseDTO> categories = categoryMapper.toResponseDTOList(categoryRepository.findAll());
        redisService.set(REDIS_KEY_ALL_CATEGORIES, categories, 1, java.util.concurrent.TimeUnit.HOURS);
        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<CategoryResponseDTO> getAllCategoriesPaginated(int page, int limit,
            String sortBy, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir
                .equalsIgnoreCase(org.springframework.data.domain.Sort.Direction.ASC.name())
                        ? org.springframework.data.domain.Sort.by(sortBy).ascending()
                        : org.springframework.data.domain.Sort.by(sortBy).descending();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1,
                limit, sort);

        org.springframework.data.domain.Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponseDTO> items = categoryMapper.toResponseDTOList(categoryPage.getContent());

        com.example.DTO.Response.PaginationMeta paginationMeta = com.example.DTO.Response.PaginationMeta.builder()
                .page(categoryPage.getNumber() + 1)
                .limit(categoryPage.getSize())
                .total(categoryPage.getTotalElements())
                .totalItems(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .hasNext(categoryPage.hasNext())
                .hasPrev(categoryPage.hasPrevious())
                .build();

        return com.example.DTO.Response.PaginatedData.<CategoryResponseDTO>builder()
                .items(items)
                .meta(paginationMeta)
                .build();
    }
}
