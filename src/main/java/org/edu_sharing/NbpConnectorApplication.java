package org.edu_sharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
public class NbpConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbpConnectorApplication.class, args);
    }

}
