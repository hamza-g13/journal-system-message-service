package se.kth.journalsystem.messageservice.dto;

import java.time.LocalDateTime;

public record MessageResponse(
                Long id,
                String senderId,
                String senderUsername,
                String receiverId,
                String receiverUsername,
                String subject,
                String content,
                LocalDateTime sentAt,
                boolean isRead) {
}
