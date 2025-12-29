package se.kth.journalsystem.messageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageRequest(
                @NotNull(message = "Receiver ID is required") String receiverId,

                @NotBlank(message = "Subject is required") @Size(min = 1, max = 200, message = "Subject must be between 1 and 200 characters") String subject,

                @NotBlank(message = "Content is required") @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters") String content) {
}
