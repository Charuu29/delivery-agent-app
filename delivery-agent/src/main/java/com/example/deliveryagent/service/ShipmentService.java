package com.example.deliveryagent.service;

import com.example.deliveryagent.dto.CreateShipmentRequest;
import com.example.deliveryagent.dto.ShipmentResponse;
import com.example.deliveryagent.dto.UpdateStatusRequest;
import com.example.deliveryagent.exception.BadRequestException;
import com.example.deliveryagent.exception.DuplicateResourceException;
import com.example.deliveryagent.exception.ResourceNotFoundException;
import com.example.deliveryagent.model.Shipment;
import com.example.deliveryagent.model.ShipmentStatus;
import com.example.deliveryagent.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        String trackingNumber = request.trackingNumber().trim();

        if (shipmentRepository.existsByTrackingNumber(trackingNumber)) {
            throw new DuplicateResourceException(
                    "A shipment with tracking number '" + trackingNumber + "' already exists");
        }

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setCustomerName(request.customerName().trim());
        shipment.setCustomerMobile(request.customerMobile().trim());
        shipment.setDeliveryAddress(request.deliveryAddress().trim());
        shipment.setDestinationPincode(request.destinationPincode().trim());
        shipment.setStatus(ShipmentStatus.ASSIGNED);

        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipments(String status, String search) {
        List<ShipmentStatus> statuses = (status == null || status.isBlank())
                ? List.of(ShipmentStatus.values())
                : List.of(parseStatus(status));
        String term = (search == null) ? "" : search.trim();

        return shipmentRepository.search(statuses, term)
                .stream()
                .map(ShipmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long id) {
        return ShipmentResponse.from(findOrThrow(id));
    }

    @Transactional
    public ShipmentResponse updateStatus(Long id, UpdateStatusRequest request) {
        Shipment shipment = findOrThrow(id);
        ShipmentStatus newStatus = parseStatus(request.status());

        validateTransition(shipment.getStatus(), newStatus);

        if (newStatus == ShipmentStatus.DELIVERY_FAILED) {
            String reason = request.failureReason();
            if (reason == null || reason.isBlank()) {
                throw new BadRequestException("Failure reason is required when status is DELIVERY_FAILED");
            }
            shipment.setFailureReason(reason.trim());
        } else {
            shipment.setFailureReason(null);
        }

        shipment.setStatus(newStatus);
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    private Shipment findOrThrow(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id " + id));
    }

    private ShipmentStatus parseStatus(String value) {
        try {
            return ShipmentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status '" + value + "'. Allowed values: "
                    + Arrays.toString(ShipmentStatus.values()));
        }
    }

    private void validateTransition(ShipmentStatus current, ShipmentStatus next) {
        boolean allowed = switch (current) {
            case ASSIGNED -> next == ShipmentStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> next == ShipmentStatus.DELIVERED
                    || next == ShipmentStatus.DELIVERY_FAILED;
            case DELIVERED, DELIVERY_FAILED -> false;
        };
        if (!allowed) {
            throw new BadRequestException("Cannot change status from " + current + " to " + next);
        }
    }
}
