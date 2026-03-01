package com.example.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FriendRequestDTO {

    @NotNull(message = "friendId không được để trống")
    private UUID friendId;
}
