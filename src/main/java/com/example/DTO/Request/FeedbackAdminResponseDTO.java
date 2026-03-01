package com.example.DTO.Request;

import com.example.Enum.FeedbackStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackAdminResponseDTO {

    @NotNull(message = "status không được để trống")
    private FeedbackStatus status;

    @Size(max = 2000, message = "admin_response tối đa 2000 ký tự")
    private String adminResponse;
}
