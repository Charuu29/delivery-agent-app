package com.example.deliveryagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateShipmentRequest(

        @NotBlank(message = "Tracking number is required")
        @Size(max = 50, message = "Tracking number must be at most 50 characters")
        String trackingNumber,

        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must be at most 100 characters")
        String customerName,

        @NotBlank(message = "Customer mobile number is required")
        @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits")
        String customerMobile,

        @NotBlank(message = "Delivery address is required")
        @Size(max = 500, message = "Delivery address must be at most 500 characters")
        String deliveryAddress,

        @NotBlank(message = "Destination pincode is required")
        @Pattern(regexp = "\\d{6}", message = "Pincode must be exactly 6 digits")
        String destinationPincode
) {
}