package com.codexist.pinpoint.service;

import com.codexist.pinpoint.dto.SavePlaceRequest;
import com.codexist.pinpoint.dto.SavedPlaceResponse;
import com.codexist.pinpoint.entity.SavedPlace;
import com.codexist.pinpoint.entity.User;
import com.codexist.pinpoint.repository.SavedPlaceRepository;
import com.codexist.pinpoint.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedPlaceService {

    @Autowired
    private SavedPlaceRepository savedPlaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public SavedPlaceResponse savePlace(SavePlaceRequest request, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        if(savedPlaceRepository.existsByUserAndPlaceId(user, request.getPlaceId()))
        {
            throw new RuntimeException("Place already saved.");
        }

        SavedPlace savedPlace = new SavedPlace();
        savedPlace.setUser(user);
        savedPlace.setPlaceId(request.getPlaceId());
        savedPlace.setPlaceName(request.getPlaceName());
        savedPlace.setLatitude(request.getLatitude());
        savedPlace.setLongitude(request.getLongitude());
        savedPlace.setType(request.getType());
        savedPlace.setAddress(request.getAddress());
        savedPlace.setCustomName(request.getCustomName());

        savedPlace = savedPlaceRepository.save(savedPlace);

        return convertToResponse(savedPlace);
    }

    @Transactional
    public List<SavedPlaceResponse> getUserSavedPlaces(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        return savedPlaceRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSavedPlace(String id, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        SavedPlace savedPlace = savedPlaceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Saved place not found."));

        savedPlaceRepository.delete(savedPlace);
    }

    private SavedPlaceResponse convertToResponse(SavedPlace savedPlace){
        return new SavedPlaceResponse(
                savedPlace.getId(),
                savedPlace.getPlaceId(),
                savedPlace.getPlaceName(),
                savedPlace.getCustomName(),
                savedPlace.getLatitude(),
                savedPlace.getLongitude(),
                savedPlace.getType(),
                savedPlace.getAddress(),
                savedPlace.getCreatedAt()
        );
    }
}