package com.ridematch.driver.repository;

import com.ridematch.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository
        extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {

    @Query(
            value =
                    """
      SELECT d.*
      FROM driver d
      WHERE d.status = 'ONLINE'
        AND d.is_available = true
        AND d.is_verified = true
        AND d.latitude  BETWEEN :latMin AND :latMax
        AND d.longitude BETWEEN :lngMin AND :lngMax
        AND (
          2*6371000*ASIN(SQRT(
            POWER(SIN(RADIANS(d.latitude  - :lat)/2),2) +
            COS(RADIANS(:lat))*COS(RADIANS(d.latitude))*
            POWER(SIN(RADIANS(d.longitude - :lng)/2),2)
          ))
        ) <= :radius
      ORDER BY
          2*6371000*ASIN(SQRT(
            POWER(SIN(RADIANS(d.latitude  - :lat)/2),2) +
            COS(RADIANS(:lat))*COS(RADIANS(d.latitude))*
            POWER(SIN(RADIANS(d.longitude - :lng)/2),2)
          ))
      LIMIT 1
      """,
            nativeQuery = true)
    Driver findNearestDriverWithinBBox(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("latMin") double latMin,
            @Param("latMax") double latMax,
            @Param("lngMin") double lngMin,
            @Param("lngMax") double lngMax,
            @Param("radius") double radiusMeters);

    Optional<Driver> findByCurrentRideId(Long rideId);
}
