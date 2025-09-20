package com.ridematch.ride.repository;

import com.ridematch.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
        UPDATE Ride r
        SET r.status = 'CANCELED'
        WHERE r.status = 'REQUESTED'
          AND r.createdDate < :threshold
    """)
    void cancelExpiredRides(@Param("threshold") LocalDateTime threshold);
}
