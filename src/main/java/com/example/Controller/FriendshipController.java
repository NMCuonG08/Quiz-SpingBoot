package com.example.Controller;

import com.example.DTO.Request.FriendRequestDTO;
import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.FriendshipResponseDTO;
import com.example.Service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@Tag(name = "Friendship Controller", description = "Quản lý kết bạn: gửi lời mời, chấp nhận, hủy, xem danh sách bạn bè")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    // =============================================
    // POST /api/friends/request
    // Gửi lời mời kết bạn
    // =============================================
    @PostMapping("/request")
    @Operation(summary = "Gửi lời mời kết bạn", description = "Gửi lời mời kết bạn tới một user khác. Trạng thái sẽ là PENDING.")
    public ResponseEntity<ApiResponse<FriendshipResponseDTO>> sendFriendRequest(
            @Parameter(description = "ID của user hiện tại (người gửi)") @RequestParam UUID currentUserId,
            @Valid @RequestBody FriendRequestDTO dto) {

        FriendshipResponseDTO result = friendshipService.sendFriendRequest(currentUserId, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Đã gửi lời mời kết bạn thành công"));
    }

    // =============================================
    // PUT /api/friends/accept/{friendshipId}
    // Chấp nhận lời mời kết bạn
    // =============================================
    @PutMapping("/accept/{friendshipId}")
    @Operation(summary = "Chấp nhận lời mời kết bạn", description = "Chấp nhận lời mời kết bạn. Chỉ người nhận mới có quyền thực hiện.")
    public ResponseEntity<ApiResponse<FriendshipResponseDTO>> acceptFriendRequest(
            @Parameter(description = "ID của user hiện tại (người nhận)") @RequestParam UUID currentUserId,
            @PathVariable UUID friendshipId) {

        FriendshipResponseDTO result = friendshipService.acceptFriendRequest(currentUserId, friendshipId);
        return ResponseEntity.ok(ApiResponse.success(result, "Đã chấp nhận lời mời kết bạn"));
    }

    // =============================================
    // DELETE /api/friends/decline/{friendshipId}
    // Từ chối / hủy lời mời
    // =============================================
    @DeleteMapping("/decline/{friendshipId}")
    @Operation(summary = "Từ chối hoặc hủy lời mời kết bạn", description = "Cả người gửi lẫn người nhận đều có thể hủy khi trạng thái là PENDING.")
    public ResponseEntity<ApiResponse<Void>> declineFriendRequest(
            @RequestParam UUID currentUserId,
            @PathVariable UUID friendshipId) {

        friendshipService.declineFriendRequest(currentUserId, friendshipId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã từ chối / hủy lời mời kết bạn"));
    }

    // =============================================
    // DELETE /api/friends/unfriend/{friendId}
    // Hủy kết bạn
    // =============================================
    @DeleteMapping("/unfriend/{friendId}")
    @Operation(summary = "Hủy kết bạn (Unfriend)", description = "Xóa mối quan hệ bạn bè đang ACCEPTED giữa 2 user.")
    public ResponseEntity<ApiResponse<Void>> unfriend(
            @RequestParam UUID currentUserId,
            @PathVariable UUID friendId) {

        friendshipService.unfriend(currentUserId, friendId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã hủy kết bạn"));
    }

    // =============================================
    // POST /api/friends/block/{targetUserId}
    // Chặn người dùng
    // =============================================
    @PostMapping("/block/{targetUserId}")
    @Operation(summary = "Chặn người dùng", description = "Chặn một user. Nếu đã có quan hệ (PENDING/ACCEPTED) sẽ bị ghi đè thành BLOCKED.")
    public ResponseEntity<ApiResponse<FriendshipResponseDTO>> blockUser(
            @RequestParam UUID currentUserId,
            @PathVariable UUID targetUserId) {

        FriendshipResponseDTO result = friendshipService.blockUser(currentUserId, targetUserId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result, "Đã chặn người dùng này"));
    }

    // =============================================
    // DELETE /api/friends/unblock/{targetUserId}
    // Bỏ chặn
    // =============================================
    @DeleteMapping("/unblock/{targetUserId}")
    @Operation(summary = "Bỏ chặn người dùng", description = "Bỏ chặn user đã bị chặn trước đó.")
    public ResponseEntity<ApiResponse<Void>> unblockUser(
            @RequestParam UUID currentUserId,
            @PathVariable UUID targetUserId) {

        friendshipService.unblockUser(currentUserId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã bỏ chặn người dùng"));
    }

    // =============================================
    // GET /api/friends/{userId}
    // Lấy danh sách bạn bè
    // =============================================
    @GetMapping("/{userId}")
    @Operation(summary = "Xem danh sách bạn bè", description = "Trả về danh sách tất cả bạn bè (status = ACCEPTED) của user.")
    public ResponseEntity<ApiResponse<List<FriendshipResponseDTO>>> getFriends(
            @PathVariable UUID userId) {

        List<FriendshipResponseDTO> friends = friendshipService.getFriends(userId);
        return ResponseEntity.ok(ApiResponse.success(friends,
                "Danh sách bạn bè (" + friends.size() + " người)"));
    }

    // =============================================
    // GET /api/friends/{userId}/requests/received
    // Lời mời nhận được (inbox)
    // =============================================
    @GetMapping("/{userId}/requests/received")
    @Operation(summary = "Xem lời mời kết bạn nhận được", description = "Trả về danh sách các lời mời kết bạn đang chờ xử lý gửi đến user.")
    public ResponseEntity<ApiResponse<List<FriendshipResponseDTO>>> getReceivedRequests(
            @PathVariable UUID userId) {

        List<FriendshipResponseDTO> requests = friendshipService.getPendingReceivedRequests(userId);
        return ResponseEntity.ok(ApiResponse.success(requests,
                "Lời mời nhận được (" + requests.size() + " lời mời)"));
    }

    // =============================================
    // GET /api/friends/{userId}/requests/sent
    // Lời mời đã gửi đi (outbox)
    // =============================================
    @GetMapping("/{userId}/requests/sent")
    @Operation(summary = "Xem lời mời kết bạn đã gửi", description = "Trả về danh sách các lời mời kết bạn user đã gửi đi đang chờ xử lý.")
    public ResponseEntity<ApiResponse<List<FriendshipResponseDTO>>> getSentRequests(
            @PathVariable UUID userId) {

        List<FriendshipResponseDTO> requests = friendshipService.getPendingSentRequests(userId);
        return ResponseEntity.ok(ApiResponse.success(requests,
                "Lời mời đã gửi (" + requests.size() + " lời mời)"));
    }

    // =============================================
    // GET /api/friends/status?currentUserId=&otherUserId=
    // Kiểm tra trạng thái quan hệ
    // =============================================
    @GetMapping("/status")
    @Operation(summary = "Kiểm tra trạng thái quan hệ bạn bè", description = "Trả về trạng thái quan hệ giữa 2 user (PENDING / ACCEPTED / BLOCKED). Trả về null nếu chưa có quan hệ.")
    public ResponseEntity<ApiResponse<FriendshipResponseDTO>> getFriendshipStatus(
            @RequestParam UUID currentUserId,
            @RequestParam UUID otherUserId) {

        FriendshipResponseDTO result = friendshipService.getFriendshipStatus(currentUserId, otherUserId);
        String message = result == null ? "Chưa có quan hệ bạn bè" : "Trạng thái: " + result.getStatus();
        return ResponseEntity.ok(ApiResponse.success(result, message));
    }
}
