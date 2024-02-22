package org.edu_sharing.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class eduSharingConfig {
    @Value("${edu.sharing.baseUrl}")
    public String baseUrl;

    @Value("${edu.sharing.user}")
    public String username;

    @Value("${edu.sharing.password}")
    public String password;

    @Value("${edu.sharing.readtimeout}")
    public int readTimeout;
//    @Bean
//    ApiClient apiClient(){
//        ApiClient apiClient = new ApiClientFixes();
//        apiClient.setReadTimeout(readTimeout);
//        apiClient.setBasePath(basePath);
//        apiClient.setUsername(username);
//        apiClient.setPassword(password);
//        return apiClient;
//    }
//
//    @Bean
//    NodeV1Api nodeV1Api(ApiClient apiClient){
//        return new NodeV1Api(apiClient);
//    }


    @Bean
    public WebClient edusharingOAIWebClient(){
        return WebClient.builder()
                .baseUrl(baseUrl + "/eduservlet/oai/provider")
                .build();
    }


}
