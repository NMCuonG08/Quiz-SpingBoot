package com.example.WebSocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
public class RoomWebSocketController {

        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RoomWebSocketController.class);
        private final SocketIOServer server;
        private final com.example.Security.JwtUtils jwtUtils;
        private final com.example.Repository.UserRepository userRepository;
        private final com.example.Repository.QuizRoomRepository quizRoomRepository;

        @Autowired
        public RoomWebSocketController(SocketIOServer server,
                        com.example.Security.JwtUtils jwtUtils,
                        com.example.Repository.UserRepository userRepository,
                        com.example.Repository.QuizRoomRepository quizRoomRepository) {
                this.server = server;
                this.jwtUtils = jwtUtils;
                this.userRepository = userRepository;
                this.quizRoomRepository = quizRoomRepository;
        }

        private static final String ROOM_PREFIX = "room:";

        @PostConstruct
        public void init() {
                server.addConnectListener(onConnected());
                server.addDisconnectListener(onDisconnected());

                server.addEventListener("join_room", Map.class, onJoinRoom());
                server.addEventListener("leave_room", Map.class, onLeaveRoom());
                server.addEventListener("send_message", Map.class, onSendMessage());
                server.addEventListener("get_participants", Map.class, onGetParticipants());
                server.addEventListener("get_messages", Map.class, onGetMessages());
                server.addEventListener("get_room_status", Map.class, onGetRoomStatus());
                server.addEventListener("invite_friends", Map.class, onInviteFriends());
        }

        private ConnectListener onConnected() {
                return client -> {
                        String authHeader = client.getHandshakeData().getHttpHeaders().get("Authorization");
                        String token = null;

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                token = authHeader.substring(7);
                        } else {
                                // Check Query param or auth field
                                token = client.getHandshakeData().getSingleUrlParam("token");
                        }

                        if (token == null || !jwtUtils.validateJwtToken(token)) {
                                logger.warn("Client connection refused: Invalid or missing token, sessionId: {}",
                                                client.getSessionId());
                                // client.disconnect(); // Netty-socketio connection listener might not allow
                                // direct disconnect here easily
                                return;
                        }

                        String username = jwtUtils.getUserNameFromJwtToken(token);
                        com.example.Entity.User user = userRepository.findByUsername(username).orElse(null);

                        if (user == null) {
                                logger.error("User not found for token: {}", username);
                                return;
                        }

                        client.set("userId", user.getId().toString());
                        client.set("username", user.getUsername());
                        client.set("avatarUr", user.getAvatar());

                        logger.info("🔌 Room WebSocket connected & authenticated: {} (User: {})", client.getSessionId(),
                                        username);
                };
        }

        private DisconnectListener onDisconnected() {
                return client -> {
                        String userId = client.get("userId");
                        logger.info("🔌 Room WebSocket disconnected: {} (User: {})", client.getSessionId(), userId);

                        // Cleanup logic similar to handleDisconnect in check.ts
                        for (String room : client.getAllRooms()) {
                                if (room.startsWith(ROOM_PREFIX)) {
                                        String roomId = room.substring(ROOM_PREFIX.length());

                                        // Notify others
                                        Map<String, Object> userLeft = new HashMap<>();
                                        userLeft.put("userId", userId);
                                        userLeft.put("roomId", roomId);
                                        userLeft.put("message", "User left the room");
                                        server.getRoomOperations(room).sendEvent("user_left", userLeft);

                                        // In many implementations, you'd also broadcast an updated participants_list
                                        // here
                                }
                        }
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onJoinRoom() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        if (roomId == null) {
                                Map<String, String> err = new HashMap<>();
                                err.put("error", "Room ID is required");
                                client.sendEvent("room_join_error", err);
                                return;
                        }

                        String userId = client.get("userId");
                        if (userId == null)
                                return;

                        String socketRoom = ROOM_PREFIX + roomId;
                        client.joinRoom(socketRoom);
                        logger.info("🏠 Join room request: Client {} joined {}", userId, socketRoom);

                        // Emit room_joined
                        Map<String, Object> response = new HashMap<>();
                        response.put("roomId", roomId);
                        response.put("socketRoom", socketRoom);
                        response.put("message", "Successfully joined room");
                        client.sendEvent("room_joined", response);

                        // Broadcast user_joined
                        Map<String, Object> userJoined = new HashMap<>();
                        userJoined.put("userId", userId);
                        userJoined.put("roomId", roomId);
                        userJoined.put("message", "User joined the room");
                        server.getRoomOperations(socketRoom).sendEvent("user_joined", userJoined);

                        // Ideally fetch participants and broadcast participants_list
                        broadcastParticipants(socketRoom, roomId);
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onLeaveRoom() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        if (roomId == null)
                                return;

                        String userId = client.get("userId");
                        String socketRoom = ROOM_PREFIX + roomId;
                        client.leaveRoom(socketRoom);

                        Map<String, Object> response = new HashMap<>();
                        response.put("roomId", roomId);
                        response.put("message", "Successfully left room");
                        client.sendEvent("room_left", response);

                        Map<String, Object> userLeft = new HashMap<>();
                        userLeft.put("userId", userId);
                        userLeft.put("roomId", roomId);
                        userLeft.put("message", "User left the room");
                        server.getRoomOperations(socketRoom).sendEvent("user_left", userLeft);

                        broadcastParticipants(socketRoom, roomId);
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onSendMessage() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        String msg = (String) data.get("message");
                        if (roomId == null || msg == null)
                                return;

                        String userId = client.get("userId");
                        String username = client.get("username");
                        String avatar = client.get("avatarUr");
                        String socketRoom = ROOM_PREFIX + roomId;

                        Map<String, Object> messageData = new HashMap<>();
                        messageData.put("id", "msg_" + System.currentTimeMillis() + "_"
                                        + UUID.randomUUID().toString().substring(0, 8));
                        messageData.put("room_id", roomId);
                        messageData.put("user_id", userId);
                        messageData.put("username", username);
                        messageData.put("message", msg);
                        messageData.put("message_type", "text");
                        messageData.put("created_at", java.time.ZonedDateTime.now().toString());
                        messageData.put("avatar_url", avatar);

                        server.getRoomOperations(socketRoom).sendEvent("room_message", messageData);
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onGetParticipants() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        if (roomId == null)
                                return;
                        broadcastParticipants(null, roomId, client);
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onGetMessages() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        if (roomId == null)
                                return;
                        client.sendEvent("messages_list", new ArrayList<>());
                };
        }

        @SuppressWarnings("rawtypes")
        private DataListener<Map> onGetRoomStatus() {
                return (client, data, ackSender) -> {
                        String roomIdStr = (String) data.get("roomId");
                        if (roomIdStr == null)
                                return;

                        try {
                                UUID roomId = UUID.fromString(roomIdStr);
                                com.example.Entity.QuizRoom room = quizRoomRepository.findById(roomId).orElse(null);
                                if (room == null)
                                        return;

                                Map<String, Object> status = new HashMap<>();
                                status.put("roomId", room.getId());
                                status.put("roomCode", room.getRoom_code());
                                status.put("status", room.getStatus());
                                status.put("currentParticipants", room.getCurrent_participants());
                                status.put("maxParticipants", room.getMax_participants());
                                status.put("isPrivate", room.getIs_private());
                                // ... map participants if needed
                                client.sendEvent("room_status", status);
                        } catch (Exception e) {
                                logger.error("Error fetching room status", e);
                        }
                };
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private DataListener<Map> onInviteFriends() {
                return (client, data, ackSender) -> {
                        String roomId = (String) data.get("roomId");
                        List<String> friendIds = (List<String>) data.get("friendIds");
                        if (roomId == null || friendIds == null)
                                return;

                        logger.info("📧 Inviting {} friends to room {}", friendIds.size(), roomId);
                        for (String fId : friendIds) {
                                // Find client for this user ID if online
                                for (var c : server.getAllClients()) {
                                        if (fId.equals(c.get("userId"))) {
                                                Map<String, Object> invitation = new HashMap<>();
                                                invitation.put("roomId", roomId);
                                                invitation.put("message", "You've been invited to join a room");
                                                c.sendEvent("room_invitation", invitation);
                                        }
                                }
                        }
                };
        }

        private void broadcastParticipants(String socketRoom, String roomId) {
                broadcastParticipants(socketRoom, roomId, null);
        }

        private void broadcastParticipants(String socketRoom, String roomId,
                        com.corundumstudio.socketio.SocketIOClient singleClient) {
                if (socketRoom == null)
                        socketRoom = ROOM_PREFIX + roomId;

                List<Map<String, Object>> parts = new ArrayList<>();
                for (var c : server.getRoomOperations(socketRoom).getClients()) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("user_id", c.get("userId"));
                        p.put("username", c.get("username"));
                        p.put("avatar_url", c.get("avatarUr"));
                        p.put("is_ready", true); // Default or fetch from DB
                        parts.add(p);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("roomId", roomId);
                response.put("participants", parts);

                if (singleClient != null) {
                        singleClient.sendEvent("participants_list", response);
                } else {
                        server.getRoomOperations(socketRoom).sendEvent("participants_list", response);
                }
        }
}
