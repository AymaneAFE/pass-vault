package passvault.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    @Value("${gateway.services.auth-service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient authServiceClient(RestClient.Builder builder) {
        return builder
                .baseUrl(authServiceUrl)
                .build();
    }
}
