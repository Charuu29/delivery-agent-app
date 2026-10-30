package com.example.deliveryagent.repository;

import com.example.deliveryagent.model.Shipment;
import com.example.deliveryagent.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    boolean existsByTrackingNumber(String trackingNumber);

    @Query("""
            SELECT s FROM Shipment s
            WHERE s.status IN :statuses
              AND (LOWER(s.trackingNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.customerName) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY s.createdAt DESC
            """)
    List<Shipment> search(@Param("statuses") List<ShipmentStatus> statuses,
                          @Param("search") String search);
}
