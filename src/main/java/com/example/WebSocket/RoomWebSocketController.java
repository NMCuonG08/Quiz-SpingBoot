package com.example.WebSocket;

import com.example.DTO.WebSocket.RoomEventPayload;
import com.example.DTO.WebSocket.WsMessage;
import com.example.Repository.QuizSessionRepository;
import com.example.Entity.QuizSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Optional;

/**
 * WebSocket Controller xử lý các event real-time cho Quiz Room.
 * 
 * ===== LUỒNG HOẠT ĐỘNG =====
 * 
 * 1. Client connects tới: ws://localhost:8080/ws
 * 
 * 2. Client subscribe channel của phòng:
 * /topic/room/{sessionCode} <- nhận broadcast (tất cả thấy)
 * /user/queue/room/{sessionCode} <- nhận message riêng tư (chỉ mình thấy)
 * 
 * 3. Client gửi event lên server:
 * /app/room/{sessionCode}/join <- tham gia phòng
 * /app/room/{sessionCode}/leave <- rời phòng
 * /app/room/{sessionCode}/start <- host bắt đầu
 * /app/room/{sessionCode}/answer <- nộp đáp án
 * /app/room/{sessionCode}/next <- host chuyển câu kế tiếp
 * /app/room/{sessionCode}/end <- host kết thúc session
 */
@Controller
public class RoomWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final QuizSessionRepository quizSessionRepository;

    @Autowired
    public RoomWebSocketController(SimpMessagingTemplate messagingTemplate,
            QuizSessionRepository quizSessionRepository) {
        this.messagingTemplate = messagingTemplate;
        this.quizSessionRepository = quizSessionRepository;
    }

    /**
     * Event: User join vào phòng
     * Client gửi tới: /app/room/{sessionCode}/join
     * Broadcast tới: /topic/room/{sessionCode}
     */
    @MessageMapping("/room/{sessionCode}/join")
    public void joinRoom(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message,
            SimpMessageHeaderAccessor headerAccessor) {

        String username = message.getFrom() != null ? message.getFrom() : "Unknown";

        // Lưu sessionCode vào WebSocket session attribute để track khi disconnect
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("sessionCode", sessionCode);
            headerAccessor.getSessionAttributes().put("username", username);
        }

        WsMessage<RoomEventPayload> broadcast = WsMessage.<RoomEventPayload>builder()
                .event("USER_JOINED")
                .sessionCode(sessionCode)
                .from(username)
                .payload(message.getPayload())
                .build();

        // Broadcast cho cả phòng
        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, broadcast);

        System.out.println("[WS] " + username + " đã vào phòng: " + sessionCode);
    }

    /**
     * Event: User rời phòng
     * Client gửi tới: /app/room/{sessionCode}/leave
     */
    @MessageMapping("/room/{sessionCode}/leave")
    public void leaveRoom(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message) {

        WsMessage<RoomEventPayload> broadcast = WsMessage.<RoomEventPayload>builder()
                .event("USER_LEFT")
                .sessionCode(sessionCode)
                .from(message.getFrom())
                .payload(message.getPayload())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, broadcast);

        System.out.println("[WS] " + message.getFrom() + " đã rời phòng: " + sessionCode);
    }

    /**
     * Event: Host bắt đầu quiz
     * Client gửi tới: /app/room/{sessionCode}/start
     */
    @MessageMapping("/room/{sessionCode}/start")
    public void startSession(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message) {

        WsMessage<RoomEventPayload> broadcast = WsMessage.<RoomEventPayload>builder()
                .event("SESSION_STARTED")
                .sessionCode(sessionCode)
                .from(message.getFrom())
                .payload(message.getPayload())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, broadcast);

        System.out.println("[WS] Session started: " + sessionCode);
    }

    /**
     * Event: Participant nộp đáp án
     * Client gửi tới: /app/room/{sessionCode}/answer
     * Chỉ gửi lại cho đúng user đó xác nhận, và host nhận thống kê
     */
    @MessageMapping("/room/{sessionCode}/answer")
    public void submitAnswer(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message) {

        String userId = message.getFrom();

        // Gửi xác nhận riêng về cho người gửi
        WsMessage<RoomEventPayload> ack = WsMessage.<RoomEventPayload>builder()
                .event("ANSWER_RECEIVED")
                .sessionCode(sessionCode)
                .from("server")
                .payload(message.getPayload())
                .build();

        messagingTemplate.convertAndSendToUser(userId, "/queue/room/" + sessionCode, ack);

        // Broadcast cho host biết ai đã trả lời (không kèm đáp án)
        RoomEventPayload hostPayload = RoomEventPayload.builder()
                .userId(message.getPayload() != null ? message.getPayload().getUserId() : null)
                .username(message.getFrom())
                .sessionCode(sessionCode)
                .build();

        WsMessage<RoomEventPayload> hostBroadcast = WsMessage.<RoomEventPayload>builder()
                .event("PARTICIPANT_ANSWERED")
                .sessionCode(sessionCode)
                .from(userId)
                .payload(hostPayload)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, hostBroadcast);
    }

    /**
     * Event: Host chuyển sang câu hỏi tiếp theo
     * Client gửi tới: /app/room/{sessionCode}/next
     */
    @MessageMapping("/room/{sessionCode}/next")
    public void nextQuestion(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message) {

        WsMessage<RoomEventPayload> broadcast = WsMessage.<RoomEventPayload>builder()
                .event("NEXT_QUESTION")
                .sessionCode(sessionCode)
                .from(message.getFrom())
                .payload(message.getPayload())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, broadcast);
    }

    /**
     * Event: Host kết thúc session, broadcast kết quả
     * Client gửi tới: /app/room/{sessionCode}/end
     */
    @MessageMapping("/room/{sessionCode}/end")
    public void endSession(@DestinationVariable String sessionCode,
            @Payload WsMessage<RoomEventPayload> message) {

        WsMessage<RoomEventPayload> broadcast = WsMessage.<RoomEventPayload>builder()
                .event("SESSION_ENDED")
                .sessionCode(sessionCode)
                .from(message.getFrom())
                .payload(message.getPayload())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + sessionCode, broadcast);

        System.out.println("[WS] Session ended: " + sessionCode);
    }
}
