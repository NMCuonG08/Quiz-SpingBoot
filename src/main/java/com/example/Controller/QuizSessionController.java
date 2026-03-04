package com.example.Controller;

import com.example.DTO.Request.QuizSessionRequestDTO;
import com.example.DTO.Response.QuizSessionResponseDTO;
import com.example.Service.QuizSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.Security.UserDetailsImpl;

@RestController
@RequestMapping("/api/quiz-sessions")
@Tag(name = "Quiz Session Controller", description = "Endpoints for Quiz Session operations")
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    @Autowired
    public QuizSessionController(QuizSessionService quizSessionService) {
        this.quizSessionService = quizSessionService;
    }

    @PostMapping
    @Operation(summary = "Create a new Quiz Session")
    public ResponseEntity<QuizSessionResponseDTO> createQuizSession(@RequestBody QuizSessionRequestDTO dto) {
        return new ResponseEntity<>(quizSessionService.createQuizSession(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Quiz Session")
    public ResponseEntity<QuizSessionResponseDTO> updateQuizSession(@PathVariable("id") UUID id,
            @RequestBody QuizSessionRequestDTO dto) {
        return new ResponseEntity<>(quizSessionService.updateQuizSession(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Quiz Session by ID")
    public ResponseEntity<Void> deleteQuizSession(@PathVariable("id") UUID id) {
        quizSessionService.deleteQuizSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Quiz Session by ID")
    public ResponseEntity<QuizSessionResponseDTO> getQuizSessionById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(quizSessionService.getQuizSessionById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get All Quiz Sessions")
    public ResponseEntity<List<QuizSessionResponseDTO>> getAllQuizSessions() {
        return new ResponseEntity<>(quizSessionService.getAllQuizSessions(), HttpStatus.OK);
    }

    @GetMapping("/host/{hostId}")
    @Operation(summary = "Get Quiz Sessions by Host ID")
    public ResponseEntity<List<QuizSessionResponseDTO>> getQuizSessionsByHostId(@PathVariable("hostId") UUID hostId) {
        return new ResponseEntity<>(quizSessionService.getQuizSessionsByHostId(hostId), HttpStatus.OK);
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get Quiz Sessions by Quiz ID")
    public ResponseEntity<List<QuizSessionResponseDTO>> getQuizSessionsByQuizId(@PathVariable("quizId") UUID quizId) {
        return new ResponseEntity<>(quizSessionService.getQuizSessionsByQuizId(quizId), HttpStatus.OK);
    }

    @GetMapping("/public/slug/{slug}")
    @Operation(summary = "Create or Resume a Public Quiz Session")
    public ResponseEntity<com.example.DTO.Response.ApiResponse<com.example.DTO.Response.QuizSessionPublicResponseDTO>> createOrResumePublicSession(
            @PathVariable("slug") String slug,
            @RequestParam(required = false) UUID userId) {

        // Mocking user ID if not provided, or it should ideally come from security
        // context
        UUID finalUserId = userId != null ? userId : UUID.fromString("be2f2fbe-b6c0-4b5e-9c6e-70e290d8de39");

        com.example.DTO.Response.QuizSessionPublicResponseDTO result = quizSessionService
                .createOrResumePublicSession(slug, finalUserId);
        return ResponseEntity.ok(com.example.DTO.Response.ApiResponse.success(result));
    }

    @PostMapping("/public/slug")
    @Operation(summary = "Create or Resume a Public Quiz Session (POST body implementation)")
    public ResponseEntity<com.example.DTO.Response.ApiResponse<com.example.DTO.Response.QuizSessionPublicResponseDTO>> postCreateOrResumePublicSession(
            @RequestBody com.example.DTO.Request.QuizSessionPublicRequestDTO request) {

        // Try to get userId from token via SecurityContext
        UUID finalUserId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                finalUserId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
            }
        } catch (Exception e) {
            // Log if needed
        }

        // Fallback to request userId or mock if still null
        if (finalUserId == null) {
            finalUserId = request.getUserId() != null ? request.getUserId()
                    : UUID.fromString("be2f2fbe-b6c0-4b5e-9c6e-70e290d8de39");
        }

        String slug = request.getQuizSlug();
        com.example.DTO.Response.QuizSessionPublicResponseDTO result = quizSessionService
                .createOrResumePublicSession(slug, finalUserId);
        return ResponseEntity.ok(com.example.DTO.Response.ApiResponse.success(result));
    }

    @GetMapping("/user/all-attempts")
    @Operation(summary = "Get all quiz sessions (attempts) for the user")
    public ResponseEntity<com.example.DTO.Response.ApiResponse<List<com.example.DTO.Response.QuizAttemptResponseDTO>>> getUserAttempts(
            @RequestParam(required = false) UUID userId) {

        // Mocking user ID for now as per project convention
        UUID finalUserId = userId != null ? userId : UUID.fromString("be2f2fbe-b6c0-4b5e-9c6e-70e290d8de39");
        return ResponseEntity
                .ok(com.example.DTO.Response.ApiResponse.success(quizSessionService.getUserAttempts(finalUserId)));
    }

    @PostMapping("/{attemptId}/complete/public")
    @Operation(summary = "Complete a Public Quiz Session and calculate results")
    public ResponseEntity<com.example.DTO.Response.ApiResponse<com.example.DTO.Response.QuizAttemptResponseDTO>> completePublicQuizSession(
            @PathVariable("attemptId") UUID attemptId) {
        return ResponseEntity
                .ok(com.example.DTO.Response.ApiResponse
                        .success(quizSessionService.completePublicQuizSession(attemptId)));
    }

    @PostMapping("/{attemptId}/answers/public")
    @Operation(summary = "Submit/Update an answer for a question in a public session")
    public ResponseEntity<com.example.DTO.Response.ApiResponse<String>> submitPublicAnswer(
            @PathVariable("attemptId") UUID attemptId,
            @RequestBody com.example.DTO.Request.QuizAnswerPublicRequestDTO answerBody) {
        quizSessionService.submitPublicAnswer(attemptId, answerBody);
        return ResponseEntity.ok(com.example.DTO.Response.ApiResponse.success("Answer submitted successfully"));
    }
}
