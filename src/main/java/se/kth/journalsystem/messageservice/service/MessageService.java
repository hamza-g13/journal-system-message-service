package se.kth.journalsystem.messageservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.kth.journalsystem.messageservice.dto.MessageRequest;
import se.kth.journalsystem.messageservice.dto.MessageResponse;

import se.kth.journalsystem.messageservice.exception.ForbiddenException;
import se.kth.journalsystem.messageservice.exception.NotFoundException;
import se.kth.journalsystem.messageservice.model.Message;
import se.kth.journalsystem.messageservice.repository.MessageRepository;
import se.kth.journalsystem.messageservice.util.SecurityUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public MessageResponse sendMessage(MessageRequest request, SecurityUtils.UserInfo sender, String token) {
        Message message = new Message();
        message.setSenderId(sender.userId());
        message.setSenderUsername(sender.username());
        message.setReceiverId(request.receiverId());
        message.setSubject(request.subject());
        message.setContent(request.content());

        // Fetch receiver username from auth-service
        String receiverUsername = fetchUsername(request.receiverId(), token);
        message.setReceiverUsername(receiverUsername);

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    public List<MessageResponse> getInbox(String userId) {
        return messageRepository.findByReceiverIdOrderBySentAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getSentMessages(String userId) {
        return messageRepository.findBySenderIdOrderBySentAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));

        if (!message.getReceiverId().equals(userId)) {
            throw new ForbiddenException("You are not the recipient of this message");
        }

        message.setRead(true);
        messageRepository.save(message);
    }

    public long getUnreadCount(String userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    private String fetchUsername(String userId, String token) {
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", token); // token already has "Bearer " prefix if passed correctly
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + userId,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Map.class);

            if (response.getBody() != null && response.getBody().containsKey("username")) {
                return (String) response.getBody().get("username");
            }
        } catch (Exception e) {
            // Log error or handle it
            System.err.println("Failed to fetch user details: " + e.getMessage());
        }
        return "Unknown";
    }

    private MessageResponse mapToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getSenderUsername(),
                message.getReceiverId(),
                message.getReceiverUsername(),
                message.getSubject(),
                message.getContent(),
                message.getSentAt(),
                message.isRead());
    }
}
