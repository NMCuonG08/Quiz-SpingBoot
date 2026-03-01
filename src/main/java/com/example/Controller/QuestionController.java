package com.example.Controller;

import com.example.DTO.Request.QuestionRequestDTO;
import com.example.DTO.Response.QuestionResponseDTO;
import com.example.Service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Controller", description = "Endpoints for Question operations")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new Question", description = "Use form-data. Fill question properties individually and upload the media file.")
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return new ResponseEntity<>(questionService.createQuestion(questionRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing Question", description = "Use form-data. Fill question properties individually and upload the media file.")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable("id") UUID id,
            @ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return new ResponseEntity<>(questionService.updateQuestion(id, questionRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Question by ID")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") UUID id) {
        questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Question by ID")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(questionService.getQuestionById(id), HttpStatus.OK);
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get Questions by Quiz ID")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByQuizId(@PathVariable("quizId") UUID quizId) {
        return new ResponseEntity<>(questionService.getQuestionsByQuizId(quizId), HttpStatus.OK);
    }
}
