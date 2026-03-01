package com.example.DTO.WebSocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic message wrapper cho mọi WebSocket event.
 * Giống kiểu { event: 'JOIN_ROOM', payload: {...} } bên Socket.IO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage<T> {
    private String event; // Tên event: JOIN_ROOM, LEAVE_ROOM, START_SESSION, etc.
    private T payload; // Data kèm theo
    private String from; // userId người gửi (optional)
    private String sessionCode; // Room code
}
