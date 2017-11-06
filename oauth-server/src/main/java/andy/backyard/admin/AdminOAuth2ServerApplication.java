package andy.backyard.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
/**
 * @EnableResourceServer adds a filter of type OAuth2AuthenticationProcessingFilter automatically to the Spring Security filter chain
 */
@EnableResourceServer
public class AdminOAuth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminOAuth2ServerApplication.class, args);
    }
}