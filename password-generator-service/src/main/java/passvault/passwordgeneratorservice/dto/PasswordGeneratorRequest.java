package passvault.passwordgeneratorservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordGeneratorRequest {
    @Builder.Default
    @Min(8)
    @Max(128)
    private int length = 16;

    @Builder.Default
    private boolean includeUppercase = true;

    @Builder.Default
    private boolean includeLowercase = true;

    @Builder.Default
    private boolean includeNumbers = true;

    @Builder.Default
    private boolean includeSymbols = true;

    @Builder.Default
    private boolean excludeAmbiguous = false;

    @Builder.Default
    private String excludeCharacters = "";
}
