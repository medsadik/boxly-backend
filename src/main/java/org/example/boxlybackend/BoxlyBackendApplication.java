package org.example.boxlybackend;

import org.example.boxlybackend.config.RhnaApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RhnaApiProperties.class)  // ← register it here

public class BoxlyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxlyBackendApplication.class, args);
    }

}
