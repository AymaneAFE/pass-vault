package passvault.apigateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import passvault.apigateway.config.AuthFilterConfig;
import passvault.apigateway.dto.TokenValidationResponse;

import java.io.IOException;
import java.util.*;

/**
 * Authentication filter that intercepts all requests, validates JWT tokens
 * through the auth-service, and adds user information to request headers.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final RestClient authServiceClient;
    private final AuthFilterConfig authFilterConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthenticationFilter(RestClient authServiceClient, AuthFilterConfig authFilterConfig) {
        this.authServiceClient = authServiceClient;
        this.authFilterConfig = authFilterConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("Processing request: {} {}", method, requestPath);

        if (isOpenEndpoint(requestPath)) {
            log.debug("Open endpoint, skipping authentication: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {}", requestPath);
            sendUnauthorizedResponse(response, "Missing or invalid Authorization header");
            return;
        }

        try {
            TokenValidationResponse validationResponse = validateTokenWithAuthService(authHeader);

            if (validationResponse == null || !validationResponse.isValid()) {
                String message = validationResponse != null ? validationResponse.getMessage()
                        : "Token validation failed";
                log.warn("Token validation failed for path {}: {}", requestPath, message);
                sendUnauthorizedResponse(response, message);
                return;
            }

            log.debug("Token validated successfully for user: {}", validationResponse.getUsername());

            HttpServletRequest wrappedRequest = new HeaderMapRequestWrapper(request, validationResponse,
                    authFilterConfig);

            filterChain.doFilter(wrappedRequest, response);

        } catch (RestClientException e) {
            log.error("Error communicating with auth-service: {}", e.getMessage());
            sendServiceUnavailableResponse(response, "Authentication service unavailable");
        }
    }

    private boolean isOpenEndpoint(String requestPath) {
        return authFilterConfig.getOpenEndpoints().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private TokenValidationResponse validateTokenWithAuthService(String authHeader) {
        return authServiceClient.get()
                .uri("/api/auth/validate")
                .header(AUTHORIZATION_HEADER, authHeader)
                .retrieve()
                .body(TokenValidationResponse.class);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message));
    }

    private void sendServiceUnavailableResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"Service Unavailable\", \"message\": \"%s\"}", message));
    }

    /**
     * Request wrapper that adds user information headers to the request
     */
    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> customHeaders = new HashMap<>();

        public HeaderMapRequestWrapper(HttpServletRequest request,
                TokenValidationResponse validationResponse,
                AuthFilterConfig config) {
            super(request);

            // Add user information headers
            if (validationResponse.getUserId() != null) {
                customHeaders.put(config.getUserIdHeader(), validationResponse.getUserId().toString());
            }
            if (validationResponse.getUsername() != null) {
                customHeaders.put(config.getUsernameHeader(), validationResponse.getUsername());
            }
        }

        @Override
        public String getHeader(String name) {
            // First check custom headers
            String customHeader = customHeaders.get(name);
            if (customHeader != null) {
                return customHeader;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> headerNames = new HashSet<>(customHeaders.keySet());
            Enumeration<String> originalHeaderNames = super.getHeaderNames();
            while (originalHeaderNames.hasMoreElements()) {
                headerNames.add(originalHeaderNames.nextElement());
            }
            return Collections.enumeration(headerNames);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String customHeader = customHeaders.get(name);
            if (customHeader != null) {
                return Collections.enumeration(Collections.singletonList(customHeader));
            }
            return super.getHeaders(name);
        }
    }

}
