package passvault.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for gateway routes.
 * 
 * Define routes in application.properties like:
 * gateway.services.my-service.url=http://localhost:8085
 * gateway.services.my-service.path=/api/myservice/**
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayRoutesProperties {

    private Map<String, ServiceRoute> services = new HashMap<>();

    @Data
    public static class ServiceRoute {
        private String url;
        private String path;
    }
}
