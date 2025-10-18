package com.codexist.pinpoint.service;

import com.codexist.pinpoint.entity.PlaceSearch;
import com.codexist.pinpoint.exception.GooglePlacesApiException;
import com.codexist.pinpoint.exception.RateLimitExceededException;
import com.codexist.pinpoint.repository.PlaceSearchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Locale;
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

    public PlacesService() {
        this.webClient = WebClient.builder().build();
    }

    @Transactional
    public String searchNearbyPlaces(Double lat, Double lng, Integer rad, String type, String userIdentifier) {
        if (!rateLimitService.tryConsume(userIdentifier)) {
            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
        }

        Optional<PlaceSearch> cachedSearch = placeSearchRepository.findByValidCachedSearch(
                lat, lng, rad, type, LocalDateTime.now()
        );

        if (cachedSearch.isPresent()) {
            return cachedSearch.get().getResponse();
        }

        String response = callGooglePlacesApi(lat, lng, rad, type);

        PlaceSearch placeSearch = new PlaceSearch();
        placeSearch.setLatitude(lat);
        placeSearch.setLongitude(lng);
        placeSearch.setRadius(rad);
        placeSearch.setType(type);
        placeSearch.setResponse(response);
        placeSearchRepository.save(placeSearch);

        return response;
    }

    private String callGooglePlacesApi(Double lat, Double lng, Integer rad, String type) {
        try {
            String url = String.format(Locale.US, "%s?location=%f,%f&radius=%d&type=%s&key=%s",
                    baseUrl, lat, lng, rad, type, apiKey);

            System.out.println("Calling Google Places API: " + url);

            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Google API Error: " + e.getResponseBodyAsString());
            throw new GooglePlacesApiException(
                    "Failed to fetch data from Google Places API: " + e.getStatusCode(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error calling Google Places API: " + e.getMessage());
            throw new GooglePlacesApiException(
                    "Unexpected error occurred while calling Google Places API", e);
        }
    }

    @Transactional
    public void cleanupExpiredCache() {
        placeSearchRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}