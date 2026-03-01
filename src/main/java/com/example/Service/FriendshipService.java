package com.example.Service;

import com.example.DTO.Request.FriendRequestDTO;
import com.example.DTO.Response.FriendshipResponseDTO;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {

    /** Gửi lời mời kết bạn */
    FriendshipResponseDTO sendFriendRequest(UUID currentUserId, FriendRequestDTO dto);

    /** Chấp nhận lời mời kết bạn */
    FriendshipResponseDTO acceptFriendRequest(UUID currentUserId, UUID friendshipId);

    /** Từ chối / hủy lời mời kết bạn */
    void declineFriendRequest(UUID currentUserId, UUID friendshipId);

    /** Hủy kết bạn (unfriend) */
    void unfriend(UUID currentUserId, UUID friendId);

    /** Chặn người dùng */
    FriendshipResponseDTO blockUser(UUID currentUserId, UUID targetUserId);

    /** Bỏ chặn */
    void unblockUser(UUID currentUserId, UUID targetUserId);

    /** Lấy danh sách bạn bè (ACCEPTED) */
    List<FriendshipResponseDTO> getFriends(UUID userId);

    /** Lấy danh sách lời mời chờ xử lý GỬI ĐẾN user (inbox) */
    List<FriendshipResponseDTO> getPendingReceivedRequests(UUID userId);

    /** Lấy danh sách lời mời user đã GỬI ĐI (outbox) */
    List<FriendshipResponseDTO> getPendingSentRequests(UUID userId);

    /** Lấy trạng thái quan hệ giữa 2 user */
    FriendshipResponseDTO getFriendshipStatus(UUID currentUserId, UUID otherUserId);
}
