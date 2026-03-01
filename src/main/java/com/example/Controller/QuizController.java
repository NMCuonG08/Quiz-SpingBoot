package com.example.Controller;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Controller", description = "Endpoints for Quiz operations")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new Quiz", description = "Use form-data. Fill quiz properties individually and upload the thumbnail file.")
    public ResponseEntity<QuizResponseDTO> createQuiz(
            @ModelAttribute QuizRequestDTO quizRequestDTO) {
        return new ResponseEntity<>(quizService.createQuiz(quizRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing Quiz", description = "Use form-data. Fill quiz properties individually and upload the thumbnail file.")
    public ResponseEntity<QuizResponseDTO> updateQuiz(
            @PathVariable("id") UUID id,
            @ModelAttribute QuizRequestDTO quizRequestDTO) {
        return new ResponseEntity<>(quizService.updateQuiz(id, quizRequestDTO), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Quiz by ID")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") UUID id) {
        quizService.deleteQuiz(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @Operation(summary = "Get Quiz by ID")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(quizService.getQuizById(id), HttpStatus.OK);
    }

    // @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get All Quizzes")
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzes() {
        return new ResponseEntity<>(quizService.getAllQuizzes(), HttpStatus.OK);
    }
}
