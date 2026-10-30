package com.example.deliveryagent.controller;

import com.example.deliveryagent.dto.CreateShipmentRequest;
import com.example.deliveryagent.dto.ShipmentResponse;
import com.example.deliveryagent.dto.UpdateStatusRequest;
import com.example.deliveryagent.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(@Valid @RequestBody CreateShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(request));
    }

    @GetMapping
    public List<ShipmentResponse> list(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) String search) {
        return shipmentService.getShipments(status, search);
    }

    @GetMapping("/{id}")
    public ShipmentResponse getOne(@PathVariable Long id) {
        return shipmentService.getShipment(id);
    }

    @PatchMapping("/{id}/status")
    public ShipmentResponse updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody UpdateStatusRequest request) {
        return shipmentService.updateStatus(id, request);
    }
}
