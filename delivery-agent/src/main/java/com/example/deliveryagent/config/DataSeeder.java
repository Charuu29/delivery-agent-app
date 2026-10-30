package com.example.deliveryagent.config;

import com.example.deliveryagent.model.Shipment;
import com.example.deliveryagent.model.ShipmentStatus;
import com.example.deliveryagent.repository.ShipmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedShipments(ShipmentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(create("TRK1001", "Ravi Kumar", "9876543210",
                    "12 Gandhi Street, Vellore", "632001", ShipmentStatus.ASSIGNED, null));
            repository.save(create("TRK1002", "Priya Sharma", "9123456780",
                    "45 Anna Nagar, Chennai", "600040", ShipmentStatus.OUT_FOR_DELIVERY, null));
            repository.save(create("TRK1003", "Arun Prakash", "9988776655",
                    "7 Race Course Road, Coimbatore", "641018", ShipmentStatus.DELIVERED, null));
            repository.save(create("TRK1004", "Meena Devi", "9090909090",
                    "23 Temple Street, Madurai", "625001", ShipmentStatus.DELIVERY_FAILED,
                    "Customer not available"));
            repository.save(create("TRK1005", "Karthik Raj", "9345678123",
                    "88 Market Road, Jolarpettai", "635851", ShipmentStatus.ASSIGNED, null));
        };
    }

    private Shipment create(String tracking, String name, String mobile, String address,
                            String pincode, ShipmentStatus status, String failureReason) {
        Shipment s = new Shipment();
        s.setTrackingNumber(tracking);
        s.setCustomerName(name);
        s.setCustomerMobile(mobile);
        s.setDeliveryAddress(address);
        s.setDestinationPincode(pincode);
        s.setStatus(status);
        s.setFailureReason(failureReason);
        return s;
    }
}
