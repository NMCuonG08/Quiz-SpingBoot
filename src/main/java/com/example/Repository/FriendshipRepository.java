package com.example.Repository;

import com.example.Entity.Friendship;
import com.example.Enum.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    // Tìm friendship giữa 2 user (cả 2 chiều)
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.userId = :userId AND f.friendId = :friendId) OR " +
            "(f.userId = :friendId AND f.friendId = :userId)")
    Optional<Friendship> findBetweenUsers(@Param("userId") UUID userId,
            @Param("friendId") UUID friendId);

    // Lấy tất cả friends đã ACCEPTED của user
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.userId = :userId OR f.friendId = :userId) AND f.status = :status")
    List<Friendship> findAllByUserIdAndStatus(@Param("userId") UUID userId,
            @Param("status") FriendshipStatus status);

    // Lấy danh sách lời mời đang chờ gửi đến user (user là người nhận)
    List<Friendship> findByFriendIdAndStatus(UUID friendId, FriendshipStatus status);

    // Lấy danh sách lời mời user đã gửi đi
    List<Friendship> findByUserIdAndStatus(UUID userId, FriendshipStatus status);

    // Kiểm tra 2 user có phải bạn bè chưa
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f WHERE " +
            "((f.userId = :userId AND f.friendId = :friendId) OR " +
            "(f.userId = :friendId AND f.friendId = :userId)) AND f.status = 'ACCEPTED'")
    boolean areFriends(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    // Đếm số friends của user
    @Query("SELECT COUNT(f) FROM Friendship f WHERE " +
            "(f.userId = :userId OR f.friendId = :userId) AND f.status = 'ACCEPTED'")
    long countFriends(@Param("userId") UUID userId);
}
