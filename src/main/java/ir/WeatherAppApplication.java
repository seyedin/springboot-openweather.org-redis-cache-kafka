package ir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class WeatherAppApplication {
    private static final Logger log = LoggerFactory.getLogger(WeatherAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WeatherAppApplication.class, args);
        log.info("\n Swagger OpenAPI documentation is available at: http://localhost:8080/index.html\n");
        log.info("\n The Spring Boot Application is now up and running on port 8080! ");
        log.info("\n Swagger OpenAPI documentation is available at: http://localhost:8080/swagger-ui.html");
        log.info("\n Enjoy exploring your API! ");
    }

}
