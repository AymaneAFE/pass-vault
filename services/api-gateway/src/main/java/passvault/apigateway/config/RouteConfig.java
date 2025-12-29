package passvault.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Dynamic route configuration for the API Gateway.
 * Routes are loaded from application.properties using GatewayRoutesProperties.
 * 
 * To add a new service, simply add to application.properties:
 * gateway.services.my-service.url=http://localhost:8085
 * gateway.services.my-service.path=/api/myservice/**
 */
@Configuration
public class RouteConfig {

    private final GatewayRoutesProperties routesProperties;

    public RouteConfig(GatewayRoutesProperties routesProperties) {
        this.routesProperties = routesProperties;
    }

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        RouterFunction<ServerResponse> combined = null;

        for (var entry : routesProperties.getServices().entrySet()) {
            String serviceName = entry.getKey();
            String serviceUrl = entry.getValue().getUrl();
            String servicePath = entry.getValue().getPath();

            RouterFunction<ServerResponse> routeFunc = route(serviceName)
                    .route(path(servicePath), http())
                    .before(uri(serviceUrl))
                    .build();

            if (combined == null) {
                combined = routeFunc;
            } else {
                combined = combined.and(routeFunc);
            }
        }

        // Return empty route if no services configured
        if (combined == null) {
            combined = route("empty").build();
        }

        return combined;
    }
}
