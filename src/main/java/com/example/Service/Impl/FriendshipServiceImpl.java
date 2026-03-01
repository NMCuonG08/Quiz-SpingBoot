package com.example.Service.Impl;

import com.example.DTO.Request.FriendRequestDTO;
import com.example.DTO.Response.FriendshipResponseDTO;
import com.example.DTO.Response.FriendshipResponseDTO.UserSummaryDTO;
import com.example.Entity.Friendship;
import com.example.Entity.User;
import com.example.Enum.FriendshipStatus;
import com.example.Exception.ResourceNotFoundException;
import com.example.Repository.FriendshipRepository;
import com.example.Repository.UserRepository;
import com.example.Service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // =============================================
    // Gửi lời mời kết bạn
    // =============================================
    @Override
    public FriendshipResponseDTO sendFriendRequest(UUID currentUserId, FriendRequestDTO dto) {
        UUID friendId = dto.getFriendId();

        if (currentUserId.equals(friendId)) {
            throw new IllegalArgumentException("Không thể tự gửi lời mời kết bạn cho chính mình");
        }

        // Kiểm tra user và friend tồn tại
        User currentUser = findUserById(currentUserId);
        User friend = findUserById(friendId);

        // Kiểm tra đã có friendship chưa
        Optional<Friendship> existing = friendshipRepository.findBetweenUsers(currentUserId, friendId);
        if (existing.isPresent()) {
            Friendship existingFriendship = existing.get();
            switch (existingFriendship.getStatus()) {
                case ACCEPTED -> throw new IllegalStateException("Hai người đã là bạn bè");
                case PENDING -> throw new IllegalStateException("Lời mời kết bạn đã tồn tại, đang chờ xử lý");
                case BLOCKED -> throw new IllegalStateException("Không thể gửi lời mời, người dùng đã bị chặn");
            }
        }

        Friendship friendship = new Friendship();
        friendship.setUserId(currentUserId);
        friendship.setFriendId(friendId);
        friendship.setStatus(FriendshipStatus.PENDING);

        Friendship saved = friendshipRepository.save(friendship);
        return mapToDTO(saved, currentUser, friend);
    }

    // =============================================
    // Chấp nhận lời mời
    // =============================================
    @Override
    public FriendshipResponseDTO acceptFriendRequest(UUID currentUserId, UUID friendshipId) {
        Friendship friendship = findFriendshipById(friendshipId);

        // Chỉ người nhận mới được chấp nhận
        if (!friendship.getFriendId().equals(currentUserId)) {
            throw new IllegalStateException("Bạn không có quyền chấp nhận lời mời này");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Lời mời không ở trạng thái PENDING");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        Friendship saved = friendshipRepository.save(friendship);

        User sender = findUserById(saved.getUserId());
        User receiver = findUserById(saved.getFriendId());
        return mapToDTO(saved, sender, receiver);
    }

    // =============================================
    // Từ chối / hủy lời mời
    // =============================================
    @Override
    public void declineFriendRequest(UUID currentUserId, UUID friendshipId) {
        Friendship friendship = findFriendshipById(friendshipId);

        // Cả người gửi lẫn người nhận đều có thể hủy
        boolean isSender = friendship.getUserId().equals(currentUserId);
        boolean isReceiver = friendship.getFriendId().equals(currentUserId);
        if (!isSender && !isReceiver) {
            throw new IllegalStateException("Bạn không có quyền hủy lời mời này");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể từ chối lời mời đang PENDING");
        }

        friendshipRepository.delete(friendship);
    }

    // =============================================
    // Hủy kết bạn (Unfriend)
    // =============================================
    @Override
    public void unfriend(UUID currentUserId, UUID friendId) {
        Friendship friendship = friendshipRepository.findBetweenUsers(currentUserId, friendId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Friendship", "users", currentUserId + "-" + friendId));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Hai người chưa là bạn bè");
        }

        friendshipRepository.delete(friendship);
    }

    // =============================================
    // Chặn người dùng
    // =============================================
    @Override
    public FriendshipResponseDTO blockUser(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Không thể tự chặn chính mình");
        }

        User blocker = findUserById(currentUserId);
        User target = findUserById(targetUserId);

        Optional<Friendship> existing = friendshipRepository.findBetweenUsers(currentUserId, targetUserId);

        Friendship friendship;
        if (existing.isPresent()) {
            friendship = existing.get();
            // Chỉ người chủ động block mới được set lại
            friendship.setUserId(currentUserId);
            friendship.setFriendId(targetUserId);
            friendship.setStatus(FriendshipStatus.BLOCKED);
        } else {
            friendship = new Friendship();
            friendship.setUserId(currentUserId);
            friendship.setFriendId(targetUserId);
            friendship.setStatus(FriendshipStatus.BLOCKED);
        }

        Friendship saved = friendshipRepository.save(friendship);
        return mapToDTO(saved, blocker, target);
    }

    // =============================================
    // Bỏ chặn
    // =============================================
    @Override
    public void unblockUser(UUID currentUserId, UUID targetUserId) {
        Friendship friendship = friendshipRepository.findBetweenUsers(currentUserId, targetUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Friendship", "users", currentUserId + "-" + targetUserId));

        if (friendship.getStatus() != FriendshipStatus.BLOCKED) {
            throw new IllegalStateException("Người dùng này không bị chặn");
        }
        if (!friendship.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("Bạn không có quyền bỏ chặn người này");
        }

        friendshipRepository.delete(friendship);
    }

    // =============================================
    // Lấy danh sách bạn bè
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getFriends(UUID userId) {
        findUserById(userId); // validate user exists
        List<Friendship> friendships = friendshipRepository
                .findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);

        return friendships.stream()
                .map(f -> {
                    User sender = findUserById(f.getUserId());
                    User receiver = findUserById(f.getFriendId());
                    return mapToDTO(f, sender, receiver);
                })
                .collect(Collectors.toList());
    }

    // =============================================
    // Lấy lời mời gửi đến (inbox)
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getPendingReceivedRequests(UUID userId) {
        findUserById(userId);
        return friendshipRepository.findByFriendIdAndStatus(userId, FriendshipStatus.PENDING)
                .stream()
                .map(f -> {
                    User sender = findUserById(f.getUserId());
                    User receiver = findUserById(f.getFriendId());
                    return mapToDTO(f, sender, receiver);
                })
                .collect(Collectors.toList());
    }

    // =============================================
    // Lấy lời mời đã gửi đi (outbox)
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getPendingSentRequests(UUID userId) {
        findUserById(userId);
        return friendshipRepository.findByUserIdAndStatus(userId, FriendshipStatus.PENDING)
                .stream()
                .map(f -> {
                    User sender = findUserById(f.getUserId());
                    User receiver = findUserById(f.getFriendId());
                    return mapToDTO(f, sender, receiver);
                })
                .collect(Collectors.toList());
    }

    // =============================================
    // Kiểm tra trạng thái quan hệ
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public FriendshipResponseDTO getFriendshipStatus(UUID currentUserId, UUID otherUserId) {
        findUserById(currentUserId);
        findUserById(otherUserId); // validate exists

        Optional<Friendship> optional = friendshipRepository.findBetweenUsers(currentUserId, otherUserId);
        if (optional.isEmpty()) {
            return null; // Chưa có quan hệ
        }

        Friendship f = optional.get();
        User sender = findUserById(f.getUserId());
        User receiver = findUserById(f.getFriendId());
        return mapToDTO(f, sender, receiver);
    }

    // =============================================
    // Helper methods
    // =============================================
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Friendship findFriendshipById(UUID friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship", "id", friendshipId));
    }

    private FriendshipResponseDTO mapToDTO(Friendship friendship, User sender, User receiver) {
        return FriendshipResponseDTO.builder()
                .id(friendship.getId())
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .updatedAt(friendship.getUpdatedAt())
                .sender(UserSummaryDTO.builder()
                        .id(sender.getId())
                        .username(sender.getUsername())
                        .fullName(sender.getFull_name())
                        .avatar(sender.getAvatar())
                        .build())
                .receiver(UserSummaryDTO.builder()
                        .id(receiver.getId())
                        .username(receiver.getUsername())
                        .fullName(receiver.getFull_name())
                        .avatar(receiver.getAvatar())
                        .build())
                .build();
    }
}
