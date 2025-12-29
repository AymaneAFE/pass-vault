package passvault.authservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TokenValidationResponse {

    private boolean valid;
    private String username;
    private UUID userId;
    private Set<String> roles;
    private String message;

    public TokenValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public TokenValidationResponse(boolean valid, String username, UUID userId, Set<String> roles) {
        this.valid = valid;
        this.username = username;
        this.userId = userId;
        this.roles = roles;
    }
}
