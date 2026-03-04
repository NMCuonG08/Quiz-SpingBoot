package com.example.Controller;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Entity.User;
import com.example.Repository.UserRepository;
import com.example.Service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Controller", description = "Endpoints for Quiz operations")
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;

    @Autowired
    public QuizController(QuizService quizService, UserRepository userRepository) {
        this.quizService = quizService;
        this.userRepository = userRepository;
    }

    // ⚠️ /me phải đặt TRƯỚC /{id} để tránh Spring parse "me" thành UUID
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    @Operation(summary = "Get quizzes of current logged-in user (paginated)")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getMyQuizzes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.example.Exception.AppException("USER_NOT_FOUND", "User not found"));

        com.example.DTO.Response.PaginatedData<QuizResponseDTO> result = quizService.getMyQuizzes(user.getId(), page,
                limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recently-published")
    @Operation(summary = "Get recently published public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getRecentlyPublished(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getRecentlyPublished(page, limit));
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getPopular(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getPopular(page, limit));
    }

    @GetMapping("/best-rated")
    @Operation(summary = "Get best rated public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getBestRated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getBestRated(page, limit));
    }

    @GetMapping("/easy")
    @Operation(summary = "Get easy public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getEasy(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getEasy(page, limit));
    }

    @GetMapping("/medium")
    @Operation(summary = "Get medium public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getMedium(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getMedium(page, limit));
    }

    @GetMapping("/hard")
    @Operation(summary = "Get hard public quizzes")
    public ResponseEntity<com.example.DTO.Response.PaginatedData<QuizResponseDTO>> getHard(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getHard(page, limit));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new Quiz (form-data)", description = "Use form-data when uploading thumbnail.")
    public ResponseEntity<QuizResponseDTO> createQuizFormData(
            @ModelAttribute QuizRequestDTO quizRequestDTO,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        injectCreatorId(quizRequestDTO, userDetails);
        return new ResponseEntity<>(quizService.createQuiz(quizRequestDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new Quiz (JSON)", description = "Use JSON body when no file upload needed.")
    public ResponseEntity<QuizResponseDTO> createQuizJson(
            @RequestBody QuizRequestDTO quizRequestDTO,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        injectCreatorId(quizRequestDTO, userDetails);
        return new ResponseEntity<>(quizService.createQuiz(quizRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing Quiz (form-data)", description = "Use form-data when uploading thumbnail.")
    public ResponseEntity<QuizResponseDTO> updateQuizFormData(
            @PathVariable("id") UUID id,
            @ModelAttribute QuizRequestDTO quizRequestDTO) {
        return new ResponseEntity<>(quizService.updateQuiz(id, quizRequestDTO), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing Quiz (JSON)", description = "Use JSON body when no file upload needed.")
    public ResponseEntity<QuizResponseDTO> updateQuizJson(
            @PathVariable("id") UUID id,
            @RequestBody QuizRequestDTO quizRequestDTO) {
        return new ResponseEntity<>(quizService.updateQuiz(id, quizRequestDTO), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Quiz by ID")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") UUID id) {
        quizService.deleteQuiz(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Quiz by ID")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(quizService.getQuizById(id), HttpStatus.OK);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Quiz by slug")
    public ResponseEntity<QuizResponseDTO> getQuizBySlug(@PathVariable("slug") String slug) {
        return new ResponseEntity<>(quizService.getQuizBySlug(slug), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get All Quizzes")
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzes() {
        return new ResponseEntity<>(quizService.getAllQuizzes(), HttpStatus.OK);
    }

    // ============= PRIVATE HELPERS =============

    /**
     * Lấy creatorId từ JWT token và set vào DTO.
     * FE không cần gửi creatorId nữa.
     */
    private void injectCreatorId(QuizRequestDTO dto,
            org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new com.example.Exception.AppException("USER_NOT_FOUND", "User not found"));
            dto.setCreator_id(user.getId());
        }
    }
}
