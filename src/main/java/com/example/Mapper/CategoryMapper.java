package com.example.Mapper;

import com.example.DTO.Request.CategoryRequestDTO;
import com.example.DTO.Response.CategoryResponseDTO;
import com.example.Entity.Category;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        updateEntityFromRequestDTO(category, dto);
        return category;
    }

    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .iconUrl(category.getIcon_url())
                .parentId(category.getParent_id())
                .sortOrder(category.getSort_order())
                .isActive(category.getIs_active())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequestDTO(Category category, CategoryRequestDTO dto) {
        if (category == null || dto == null) {
            return;
        }

        if (dto.getName() != null)
            category.setName(dto.getName());
        if (dto.getSlug() != null)
            category.setSlug(dto.getSlug());
        if (dto.getDescription() != null)
            category.setDescription(dto.getDescription());
        if (dto.getParentId() != null)
            category.setParent_id(dto.getParentId());
        if (dto.getSortOrder() != null)
            category.setSort_order(dto.getSortOrder());
        if (dto.getIsActive() != null)
            category.setIs_active(dto.getIsActive());
    }

    public List<CategoryResponseDTO> toResponseDTOList(List<Category> categories) {
        if (categories == null)
            return null;
        return categories.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
