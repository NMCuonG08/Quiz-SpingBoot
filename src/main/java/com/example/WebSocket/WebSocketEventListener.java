package com.example.WebSocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener để track khi User kết nối / ngắt kết nối WebSocket
 */
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("[WS] New connection: sessionId=" + headerAccessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String sessionCode = null;
        String username = null;

        if (headerAccessor.getSessionAttributes() != null) {
            sessionCode = (String) headerAccessor.getSessionAttributes().get("sessionCode");
            username = (String) headerAccessor.getSessionAttributes().get("username");
        }

        System.out.println("[WS] Disconnected: sessionId=" + sessionId
                + ", user=" + username
                + ", room=" + sessionCode);

        // Nếu cần: Có thể inject SimpMessagingTemplate vào đây để broadcast
        // "USER_LEFT" event khi user đột ngột đứt kết nối
    }
}
