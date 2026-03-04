package com.example.Service;

import com.example.DTO.Request.CategoryRequestDTO;
import com.example.DTO.Response.CategoryResponseDTO;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);

    CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO categoryRequestDTO);

    void deleteCategory(UUID id);

    CategoryResponseDTO getCategoryById(UUID id);

    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO getCategoryBySlug(String slug);

    com.example.DTO.Response.PaginatedData<CategoryResponseDTO> getAllCategoriesPaginated(int page, int limit,
            String sortBy, String sortDir);
}
