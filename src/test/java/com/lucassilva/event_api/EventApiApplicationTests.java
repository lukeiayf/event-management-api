package com.lucassilva.event_api;

import com.lucassilva.event_api.repositories.EventRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class EventApiApplicationTests {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();
    }

    @Test
    void testCreateEventSuccess() {

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("title", "test event");
        formData.add("eventUrl", "https://google.com");
        formData.add("image", new FileSystemResource(new File("src/test/java/com/lucassilva/event_api/assets/coffee.jpg")));
        formData.add("date", "1726864231000");
        formData.add("remote", "false");
        formData.add("city", "Belo Horizonte");
        formData.add("state", "MG");

        webTestClient.post()
                .uri("/api/event")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateEventBadRequest() {

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("eventUrl", "https://google.com");
        formData.add("image", new FileSystemResource(new File("src/test/java/com/lucassilva/event_api/assets/coffee.jpg")));
        formData.add("date", "1726864231000");
        formData.add("remote", "false");
        formData.add("city", "Belo Horizonte");
        formData.add("state", "MG");

        webTestClient.post()
                .uri("/api/event")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .exchange()
                .expectStatus().isBadRequest();
    }

}
