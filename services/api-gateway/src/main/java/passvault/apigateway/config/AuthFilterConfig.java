package passvault.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthFilterConfig {
    private List<String> openEndpoints = new ArrayList<>();
    private String userIdHeader = "X-User-Id";
    private String usernameHeader = "X-Username";
}
