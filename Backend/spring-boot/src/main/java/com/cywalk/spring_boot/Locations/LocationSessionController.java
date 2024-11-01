package com.cywalk.spring_boot.Locations;

import com.cywalk.spring_boot.Users.People;
import com.cywalk.spring_boot.Users.PeopleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LocationSessionController extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocationSessionController.class);
    private static LocationService locationService;
    private static PeopleService peopleService;

    private final Map<WebSocketSession, String> authenticatedPerson = new HashMap<>();

    @Autowired
    public void setPeopleService(PeopleService ps) {
        peopleService = ps;
    }

    @Autowired
    public void setLocationService(LocationService ls) {
        locationService = ls;
    }

    public static Location asLocationFromString(String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            JsonNode ms = mapper.readTree(json);
            return new Location(ms.get("latitude").asLong(), ms.get("longitude").asLong(), ms.get("elevation").asLong(), null);
        } catch (IOException e) {
            logger.error("Failed to parse location JSON: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // Extract key from the query parameter
            String query = session.getUri().getQuery();
            Map<String, String> params = Arrays.stream(query.split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(
                            p -> p[0],
                            p -> p[1]
                    ));
            String key = params.get("key");
            long keyAsLong = Long.parseLong(key);

            // Use the key as needed
            Optional<People> peopleResult = peopleService.getUserFromKey(keyAsLong);
            if (peopleResult.isEmpty()) {
                logger.error("Could not open session with key: {}", key);
            } else {
                authenticatedPerson.put(session, peopleResult.get().getUsername());
            }
        } catch (Exception e) {
            logger.error("Error parsing key from URL: {}", e.getMessage());
        }
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String username = authenticatedPerson.get(session);
        if (username == null) {
            logger.error("No user matches the current session");
            return;
        }

        Optional<People> personResult = peopleService.getUserByUsername(username);
        if (personResult.isEmpty()) {
            logger.error("The username is in the map, but not in the database.");
            return;
        }

        Location location = asLocationFromString(message.getPayload());
        if (location != null) {
            locationService.appendLocation(personResult.get(), location);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        authenticatedPerson.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("WebSocket error: {}", exception.getMessage());
    }
}
