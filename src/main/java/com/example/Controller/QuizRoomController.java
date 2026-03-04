package com.example.Controller;

import com.example.DTO.Request.QuizRoomRequestDTO;
import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.QuizRoomResponseDTO;
import com.example.Enum.RoomStatus;
import com.example.Service.QuizRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Quiz Room Controller", description = "CRUD for Quiz Rooms and listing by Quiz")
@RequiredArgsConstructor
public class QuizRoomController {

    private final QuizRoomService quizRoomService;

    @PostMapping
    @Operation(summary = "Create a new quiz room")
    public ResponseEntity<ApiResponse<QuizRoomResponseDTO>> createRoom(@RequestBody QuizRoomRequestDTO dto) {
        QuizRoomResponseDTO result = quizRoomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "Room created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<QuizRoomResponseDTO>> getRoomById(@PathVariable UUID id) {
        QuizRoomResponseDTO result = quizRoomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get room by code")
    public ResponseEntity<ApiResponse<QuizRoomResponseDTO>> getRoomByCode(@PathVariable String code) {
        QuizRoomResponseDTO result = quizRoomService.getRoomByCode(code);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/quiz/id/{quizId}")
    @Operation(summary = "Get rooms by quiz ID")
    public ResponseEntity<ApiResponse<com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO>>> getRoomsByQuizId(
            @PathVariable UUID quizId,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> result = quizRoomService.getRoomsByQuizId(quizId,
                status, page, limit);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/quiz/slug/{slug}")
    @Operation(summary = "Get rooms by quiz slug")
    public ResponseEntity<ApiResponse<com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO>>> getRoomsByQuizSlug(
            @PathVariable String slug,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> result = quizRoomService.getRoomsByQuizSlug(slug,
                status, page, limit);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details")
    public ResponseEntity<ApiResponse<QuizRoomResponseDTO>> updateRoom(
            @PathVariable UUID id,
            @RequestBody QuizRoomRequestDTO dto) {
        QuizRoomResponseDTO result = quizRoomService.updateRoom(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Room updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable UUID id) {
        quizRoomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Room deleted successfully"));
    }
}
