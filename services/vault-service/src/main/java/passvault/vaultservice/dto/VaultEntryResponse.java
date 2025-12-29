package passvault.vaultservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VaultEntryResponse {
  UUID id;
  UUID userId;
  String title;
  String username;
  String password;
  String url;
  String notes;
  CategoryResponse category;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
