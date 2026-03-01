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
}
