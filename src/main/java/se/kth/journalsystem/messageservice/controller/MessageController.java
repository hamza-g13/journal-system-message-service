package se.kth.journalsystem.messageservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.kth.journalsystem.messageservice.dto.MessageRequest;
import se.kth.journalsystem.messageservice.dto.MessageResponse;
import se.kth.journalsystem.messageservice.dto.MessageResponse;
import se.kth.journalsystem.messageservice.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    private se.kth.journalsystem.messageservice.util.SecurityUtils.UserInfo getUser(
            org.springframework.security.oauth2.jwt.Jwt jwt) {
        return se.kth.journalsystem.messageservice.util.SecurityUtils.getUserFromJwt(jwt);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
            @RequestHeader("Authorization") String token) {

        se.kth.journalsystem.messageservice.util.SecurityUtils.UserInfo currentUser = getUser(jwt);
        System.out.println("=== MessageController.sendMessage ===");
        System.out.println("Request received from user: " + (currentUser != null ? currentUser.username() : "null"));
        System.out.println("Request payload: receiverId=" + request.receiverId() + ", subject=" + request.subject());

        try {
            MessageResponse response = messageService.sendMessage(request, currentUser, token);
            System.out.println("Message sent successfully, ID: " + response.id());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in sendMessage controller: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<MessageResponse>> getInbox(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        se.kth.journalsystem.messageservice.util.SecurityUtils.UserInfo currentUser = getUser(jwt);
        System.out.println("User requesting inbox: " + currentUser.username() + ", Role: " + currentUser.role());
        return ResponseEntity.ok(messageService.getInbox(currentUser.userId()));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> getSentMessages(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        return ResponseEntity.ok(messageService.getSentMessages(getUser(jwt).userId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        messageService.markAsRead(id, getUser(jwt).userId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        return ResponseEntity.ok(messageService.getUnreadCount(getUser(jwt).userId()));
    }
}
