package com.codexist.pinpoint.repository;

import com.codexist.pinpoint.entity.PlaceSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PlaceSearchRepository extends JpaRepository<PlaceSearch, String> {
    @Query("SELECT ps FROM PlaceSearch ps WHERE " +
            "ABS(ps.latitude - :latitude) < 0.0001 AND " +
            "ABS(ps.longitude - :longitude) < 0.0001 AND " +
            "ABS(ps.radius - :radius) < 1 AND " +
            "ps.type = :type AND " +
            "ps.expiresAt > :now")
    Optional<PlaceSearch> findByValidCachedSearch(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Integer radius,
            @Param("type") String type,
            @Param("now") LocalDateTime now
    );
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}