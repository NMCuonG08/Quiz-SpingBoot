package com.example.DTO.Request;

import com.example.Enum.FeedbackType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class FeedbackRequestDTO {

    @NotNull(message = "userId không được để trống")
    private UUID userId;

    // quizId là optional — feedback có thể chung (không gắn với quiz cụ thể)
    private UUID quizId;

    @NotNull(message = "type không được để trống")
    private FeedbackType type;

    @NotBlank(message = "subject không được để trống")
    @Size(max = 200, message = "subject tối đa 200 ký tự")
    private String subject;

    @NotBlank(message = "message không được để trống")
    @Size(max = 2000, message = "message tối đa 2000 ký tự")
    private String message;

    @Min(value = 1, message = "rating tối thiểu là 1")
    @Max(value = 5, message = "rating tối đa là 5")
    private Integer rating;
}
