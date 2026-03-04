package com.example.Controller;

import com.example.DTO.Request.QuizRatingRequestDTO;
import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.PaginatedData;
import com.example.DTO.Response.QuizRatingResponseDTO;
import com.example.Entity.User;
import com.example.Repository.UserRepository;
import com.example.Service.QuizRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/feedback/ratings")
@Tag(name = "Quiz Rating Controller", description = "Quản lý đánh giá quiz: lấy danh sách, tạo mới, xóa")
@RequiredArgsConstructor
public class QuizRatingController {

    private final QuizRatingService quizRatingService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách đánh giá của một quiz", description = "Query params: quiz_id, page, limit")
    public ResponseEntity<ApiResponse<Object>> getRatings(
            @RequestParam("quiz_id") UUID quizId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {

        PaginatedData<QuizRatingResponseDTO> result = quizRatingService.getRatingsByQuizId(quizId, page, limit);
        // Matching expectation for specific data structure in ratings list
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Gửi hoặc cập nhật đánh giá quiz", description = "Yêu cầu authentication")
    public ResponseEntity<ApiResponse<QuizRatingResponseDTO>> createOrUpdateRating(
            @RequestBody QuizRatingRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new com.example.Exception.AppException("USER_NOT_FOUND", "User not found"));
            dto.setUserId(user.getId());
        }

        QuizRatingResponseDTO result = quizRatingService.createOrUpdateRating(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Đã gửi đánh giá thành công"));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Xóa đánh giá quiz", description = "Yêu cầu authentication")
    public ResponseEntity<ApiResponse<Void>> deleteRating(
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new com.example.Exception.AppException("USER_NOT_FOUND", "User not found"));
            quizRatingService.deleteRating(quizId, user.getId());
        }

        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }
}
