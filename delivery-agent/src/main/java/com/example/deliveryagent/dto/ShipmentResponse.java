package com.example.deliveryagent.dto;

import com.example.deliveryagent.model.Shipment;
import com.example.deliveryagent.model.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentResponse(
        Long id,
        String trackingNumber,
        String customerName,
        String customerMobile,
        String deliveryAddress,
        String destinationPincode,
        ShipmentStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ShipmentResponse from(Shipment s) {
        return new ShipmentResponse(
                s.getId(),
                s.getTrackingNumber(),
                s.getCustomerName(),
                s.getCustomerMobile(),
                s.getDeliveryAddress(),
                s.getDestinationPincode(),
                s.getStatus(),
                s.getFailureReason(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}