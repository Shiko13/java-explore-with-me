package ru.practicum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class StatsClient {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public List<ViewStats> getAll(LocalDateTime start, LocalDateTime end,
                                  List<String> uris, Boolean unique) {
        try {
            String query = String.format("?start=%s&end=%s&unique=%b",
                    start, end, unique);
            if (!uris.isEmpty()) {
                query += "&uris=" + String.join(",", uris);
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9090" + "/stats" + query))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                return mapper.readValue(response.body(), new TypeReference<>(){});
            }
        } catch (Exception e) {
            log.error("StatsClient can't contact with controller for getting stats, parameters: " +
                            "start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        }

        return Collections.emptyList();
    }

    public void create(EndpointHitDto endpointHitDto) {
        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest
                    .BodyPublishers
                    .ofString(mapper.writeValueAsString(endpointHitDto));
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9090" + "/hit"))
                    .POST(bodyPublisher)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            log.error("StatsClient can't contact with controller for creating hit, " +
                            "parameters: endpointHitDto={}", endpointHitDto);
        }
    }
}
