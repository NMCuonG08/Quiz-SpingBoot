package com.example.Controller;

import com.example.DTO.Request.QuestionOptionRequestDTO;
import com.example.DTO.Response.QuestionOptionResponseDTO;
import com.example.Service.QuestionOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/question-options")
@Tag(name = "Question Option Controller", description = "Endpoints for Question Option operations")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    @Autowired
    public QuestionOptionController(QuestionOptionService questionOptionService) {
        this.questionOptionService = questionOptionService;
    }

    @PostMapping
    @Operation(summary = "Create a new Question Option")
    public ResponseEntity<QuestionOptionResponseDTO> createQuestionOption(@RequestBody QuestionOptionRequestDTO dto) {
        return new ResponseEntity<>(questionOptionService.createQuestionOption(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Question Option")
    public ResponseEntity<QuestionOptionResponseDTO> updateQuestionOption(@PathVariable("id") UUID id,
            @RequestBody QuestionOptionRequestDTO dto) {
        return new ResponseEntity<>(questionOptionService.updateQuestionOption(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Question Option by ID")
    public ResponseEntity<Void> deleteQuestionOption(@PathVariable("id") UUID id) {
        questionOptionService.deleteQuestionOption(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Question Option by ID")
    public ResponseEntity<QuestionOptionResponseDTO> getQuestionOptionById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(questionOptionService.getQuestionOptionById(id), HttpStatus.OK);
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get Question Options by Question ID")
    public ResponseEntity<List<QuestionOptionResponseDTO>> getQuestionOptionsByQuestionId(
            @PathVariable("questionId") UUID questionId) {
        return new ResponseEntity<>(questionOptionService.getQuestionOptionsByQuestionId(questionId), HttpStatus.OK);
    }
}
