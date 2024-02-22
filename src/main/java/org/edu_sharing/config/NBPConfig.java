package org.edu_sharing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NBPConfig {

    @Value("${nbp.datenraum.baseurl}")
    private String baseUrl;

//    @Bean
//    public ReactiveOAuth2AuthorizedClientService oAuth2AuthorizedClientService(ReactiveClientRegistrationRepository clientRegistrations){
//        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
//        InMemoryReactiveOAuth2AuthorizedClientService
//    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrations, ReactiveOAuth2AuthorizedClientService clientService) {
        return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, clientService);
    }

    @Bean
    public WebClient nbpWebClient(ReactiveOAuth2AuthorizedClientManager clientManager){
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
        oauth.setDefaultClientRegistrationId("nbp");
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(oauth)
                .build();
    }

}
