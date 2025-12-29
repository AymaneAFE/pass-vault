package passvault.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangedEvent implements Serializable {
    private String userId;
    private String email;
    private String username;
    private Instant changedAt;
}
