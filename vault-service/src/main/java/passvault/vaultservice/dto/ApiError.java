package passvault.vaultservice.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiError {
  int status;
  String message;
  List<String> errors;
  Instant timestamp;
  String path;
}
