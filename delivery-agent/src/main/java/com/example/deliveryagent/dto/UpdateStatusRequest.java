package com.example.deliveryagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStatusRequest(

        @NotBlank(message = "Status is required")
        String status,

        @Size(max = 255, message = "Failure reason must be at most 255 characters")
        String failureReason
) {
}
