package com.example.Controller;

import com.example.DTO.Request.CategoryRequestDTO;
import com.example.DTO.Response.CategoryResponseDTO;
import com.example.Service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "Endpoints for Category operations")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new Category (form-data)", description = "Use form-data when uploading icon file.")
    public ResponseEntity<CategoryResponseDTO> createCategoryFormData(
            @ModelAttribute CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequestDTO), HttpStatus.CREATED);
    }

    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new Category (JSON)", description = "Use JSON body when no file upload needed.")
    public ResponseEntity<CategoryResponseDTO> createCategoryJson(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequestDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing Category (form-data)", description = "Use form-data when uploading icon file.")
    public ResponseEntity<CategoryResponseDTO> updateCategoryFormData(
            @PathVariable("id") UUID id,
            @ModelAttribute CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryRequestDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing Category (JSON)", description = "Use JSON body when no file upload needed.")
    public ResponseEntity<CategoryResponseDTO> updateCategoryJson(
            @PathVariable("id") UUID id,
            @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryRequestDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category by ID")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ⚠️ /slug/{slug} phải đặt TRƯỚC /{id}
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Category by Slug")
    public ResponseEntity<CategoryResponseDTO> getCategoryBySlug(@PathVariable("slug") String slug) {
        return new ResponseEntity<>(categoryService.getCategoryBySlug(slug), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category by ID")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Can optionally be paginated by providing page and limit parameters.")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        if (page != null && limit != null) {
            com.example.DTO.Response.PaginatedData<CategoryResponseDTO> paginatedData = categoryService
                    .getAllCategoriesPaginated(page, limit, sortBy, sortDir);

            return new ResponseEntity<>(
                    com.example.DTO.Response.ApiResponse.success(paginatedData, "Success"),
                    HttpStatus.OK);
        }

        // Return normal list if no pagination requested
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }
}
