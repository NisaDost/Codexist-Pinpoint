package com.codexist.pinpoint.service;

import com.codexist.pinpoint.entity.PlaceSearch;
import com.codexist.pinpoint.repository.PlaceSearchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PlacesService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    @Value("${google.maps.base-url}")
    private String baseUrl;

    @Autowired
    private PlaceSearchRepository placeSearchRepository;

    @Autowired
    private RateLimitService rateLimitService;

    private final WebClient webClient;

    public PlacesService(){
        this.webClient = WebClient.builder().build();
    }

    @Transactional
    public String searchNearbyPlaces(Double lat, Double lng, Integer rad, String userIdentifier){
        if(!rateLimitService.tryConsume(userIdentifier)){
            throw new RuntimeException("Rate limit exceeded. Please try again later.");
        }
        Optional<PlaceSearch> cachedSearch = placeSearchRepository.findByValidCachedSearch(
                lat, lng, rad, LocalDateTime.now()
        );
        if(cachedSearch.isPresent()){
            return cachedSearch.get().getResponse();
        }

        String response = callGooglePlacesApi(lat, lng, rad);

        PlaceSearch placeSearch = new PlaceSearch();
        placeSearch.setLatitude(lat);
        placeSearch.setLongitude(lng);
        placeSearch.setRadius(rad);
        placeSearch.setResponse(response);
        placeSearchRepository.save(placeSearch);

        return response;
    }

    private String callGooglePlacesApi(Double lat, Double lng, Integer rad){
        try{
            String url = String.format("%s?location=%f,%f&radius=%d&key=%s",
                    baseUrl, lat, lng, rad, apiKey);

            System.out.println("Calling Google Places API: " + url);

            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch(WebClientResponseException e){
            System.err.println("Google API Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error calling Google Places API: " + e.getMessage());
        }
    }

    @Transactional
    public void cleanupExpiredCache(){
        placeSearchRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}