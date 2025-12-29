package passvault.vaultservice.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryResponse {
  UUID id;
  UUID userId;
  String name;
}
