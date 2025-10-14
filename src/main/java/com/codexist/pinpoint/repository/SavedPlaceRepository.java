package com.codexist.pinpoint.repository;

import com.codexist.pinpoint.entity.SavedPlace;
import com.codexist.pinpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPlaceRepository extends JpaRepository<SavedPlace, String> {
    List<SavedPlace> findByUserOrderByCreatedAtDesc(User user);
    Optional<SavedPlace> findByIdAndUser(String id, User user);
    boolean existsByUserAndPlaceId(User user, String placeId);

}
