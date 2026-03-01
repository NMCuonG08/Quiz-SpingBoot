package com.example.Controller;

import com.example.DTO.Request.FeedbackAdminResponseDTO;
import com.example.DTO.Request.FeedbackRequestDTO;
import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.FeedbackResponseDTO;
import com.example.DTO.Response.PaginatedData;
import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;
import com.example.Service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@Tag(name = "Feedback Controller", description = "Quản lý feedback: tạo, cập nhật, xóa, admin phản hồi")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // =============================================
    // POST /api/feedback
    // Tạo feedback mới
    // =============================================
    @PostMapping
    @Operation(summary = "Tạo feedback mới", description = "User gửi feedback (bug, góp ý, khiếu nại, khen ngợi). " +
            "quizId là optional nếu feedback không gắn với quiz cụ thể.")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> createFeedback(
            @Valid @RequestBody FeedbackRequestDTO dto) {

        FeedbackResponseDTO result = feedbackService.createFeedback(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Feedback đã được gửi thành công"));
    }

    // =============================================
    // PUT /api/feedback/{id}
    // Cập nhật feedback (owner only, PENDING)
    // =============================================
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật feedback", description = "Chỉ chủ sở hữu mới được sửa. Chỉ sửa được khi status còn PENDING.")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> updateFeedback(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            @Valid @RequestBody FeedbackRequestDTO dto) {

        FeedbackResponseDTO result = feedbackService.updateFeedback(id, userId, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật feedback thành công"));
    }

    // =============================================
    // DELETE /api/feedback/{id}
    // Xóa feedback (owner hoặc admin)
    // =============================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa feedback", description = "Soft delete. Owner xóa feedback của mình, admin có thể xóa bất kỳ feedback nào.")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable UUID id,
            @RequestParam UUID requesterId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        feedbackService.deleteFeedback(id, requesterId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa feedback thành công"));
    }

    // =============================================
    // GET /api/feedback/{id}
    // Lấy feedback theo ID
    // =============================================
    @GetMapping("/{id}")
    @Operation(summary = "Lấy feedback theo ID")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> getFeedbackById(@PathVariable UUID id) {
        FeedbackResponseDTO result = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // =============================================
    // GET /api/feedback/user/{userId}
    // Lấy tất cả feedback của 1 user
    // =============================================
    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách feedback của user", description = "Trả về tất cả feedback user đã gửi, sắp xếp mới nhất trước.")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDTO>>> getFeedbackByUser(
            @PathVariable UUID userId) {

        List<FeedbackResponseDTO> list = feedbackService.getFeedbackByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(list,
                "Feedback của user (" + list.size() + " mục)"));
    }

    // =============================================
    // GET /api/feedback/quiz/{quizId}
    // Lấy tất cả feedback của 1 quiz
    // =============================================
    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Lấy danh sách feedback của quiz", description = "Trả về tất cả feedback gắn với quiz cụ thể.")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDTO>>> getFeedbackByQuiz(
            @PathVariable UUID quizId) {

        List<FeedbackResponseDTO> list = feedbackService.getFeedbackByQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success(list,
                "Feedback của quiz (" + list.size() + " mục)"));
    }

    // =============================================
    // GET /api/feedback
    // [Admin] Lấy tất cả feedback, có filter + phân trang
    // =============================================
    @GetMapping
    @Operation(summary = "[Admin] Lấy tất cả feedback với bộ lọc", description = "Lọc theo status, type, userId, quizId. Hỗ trợ phân trang và sắp xếp.")
    public ResponseEntity<ApiResponse<PaginatedData<FeedbackResponseDTO>>> getAllFeedback(
            @RequestParam(required = false) FeedbackStatus status,
            @RequestParam(required = false) FeedbackType type,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID quizId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PaginatedData<FeedbackResponseDTO> result = feedbackService.getAllFeedback(
                status, type, userId, quizId, page, limit, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(result, "Danh sách feedback"));
    }

    // =============================================
    // PUT /api/feedback/{id}/respond
    // [Admin] Phản hồi và cập nhật trạng thái
    // =============================================
    @PutMapping("/{id}/respond")
    @Operation(summary = "[Admin] Phản hồi feedback", description = "Admin cập nhật trạng thái (REVIEWED / RESOLVED) và gửi phản hồi cho user.")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> respondToFeedback(
            @PathVariable UUID id,
            @Valid @RequestBody FeedbackAdminResponseDTO dto) {

        FeedbackResponseDTO result = feedbackService.respondToFeedback(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Đã phản hồi feedback thành công"));
    }
}
