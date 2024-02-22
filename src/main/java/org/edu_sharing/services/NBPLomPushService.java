package org.edu_sharing.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu_sharing.models.NBPCourseDTO;
import org.edu_sharing.models.NBPCourseResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NBPLomPushService {


    private final WebClient edusharingOAIWebClient;
    private final WebClient nbpWebClient;

//    @Value("${}")


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");





    public void Test() {
        WebClient.ResponseSpec nbp = nbpWebClient.put()
                .uri(uriBuilder -> uriBuilder.pathSegment("push-connector", "api", "course", "{source}", "{id}").build("edu-sharing-editorial-network", "1"))
                .accept(MediaType.APPLICATION_JSON)
//                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId("nbp"))
                .bodyValue(new NBPCourseDTO(
                        "Englisch lernen",
                        "Aus dieser Volltext-Beschreibung sollte Umfang und Form meines Kursinhalts hervor gehen.",
                        "https://www.beispiel.de/kurse/englisch_lernen/index",
                        1234.56,
                        "D-10117 Berlin, Kapelle-Ufer 2",
                        "52.52278002340085",
                        "13.375684430248915",
                       "2023-11-23T13:33:55.123456Z",
                       "2023-11-23T13:33:55.123456Z"))
//                        LocalDateTime.parse("2023-11-23T13:33:55.123456Z", formatter),
//                        LocalDateTime.parse("2023-11-23T13:33:55.123456Z", formatter)))
                .retrieve();

        log.info("Done: {}", nbp.bodyToMono(NBPCourseResponseDTO.class)
                .onErrorResume(e-> {
                    NBPCourseResponseDTO responseBodyAs = ((WebClientResponseException) e).getResponseBodyAs(NBPCourseResponseDTO.class);
                    log.error("{}: {}", e.getMessage(), responseBodyAs);
                    return Mono.just(responseBodyAs);
                })
                .block());
    }
}
