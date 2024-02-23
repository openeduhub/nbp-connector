package org.edu_sharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(x->x.disable())
                .authorizeExchange(ex->ex.anyExchange().permitAll())
                .build();
//        http.csrf(x->x.disable())
//                //.cors().and()
//                .authorizeExchange(auth -> auth
//                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/actuator/health/*").permitAll()
//                        .requestMatchers("/actuator/prometheus").permitAll()
//                        .anyRequest().authenticated())
//                .httpBasic(withDefaults());
//        return http.build();
    }
}
