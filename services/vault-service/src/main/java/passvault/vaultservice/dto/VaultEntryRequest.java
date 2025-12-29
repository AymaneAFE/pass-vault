package passvault.vaultservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaultEntryRequest {

  @NotBlank
  @Size(max = 255)
  private String title;

  @Size(max = 255)
  private String username;

  @NotBlank
  @Size(max = 255)
  private String password;

  @Size(max = 512)
  private String url;

  @Size(max = 2000)
  private String notes;

  private UUID categoryId;
}
