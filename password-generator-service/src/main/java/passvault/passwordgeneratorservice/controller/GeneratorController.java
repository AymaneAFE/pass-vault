package passvault.passwordgeneratorservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import passvault.passwordgeneratorservice.dto.PasswordGeneratorRequest;
import passvault.passwordgeneratorservice.dto.PasswordGeneratorResponse;
import passvault.passwordgeneratorservice.dto.PasswordStrengthRequest;
import passvault.passwordgeneratorservice.dto.PasswordStrengthResponse;
import passvault.passwordgeneratorservice.service.PasswordGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final PasswordGeneratorService generatorService;

    // POST: Generate with JSON body options
    @PostMapping("/generate")
    public ResponseEntity<PasswordGeneratorResponse> generatePassword(
            @Valid @RequestBody PasswordGeneratorRequest request) {
        return ResponseEntity.ok(generatorService.generate(request));
    }

    // GET: Generate with default or query param options
    @GetMapping("/generate")
    public ResponseEntity<PasswordGeneratorResponse> generatePasswordGet(
            @RequestParam(defaultValue = "16") int length,
            @RequestParam(defaultValue = "true") boolean numbers,
            @RequestParam(defaultValue = "true") boolean symbols) {

        // Map query params to the Request DTO
        PasswordGeneratorRequest request = PasswordGeneratorRequest.builder()
                .length(length)
                .includeNumbers(numbers)
                .includeSymbols(symbols)
                .includeLowercase(true)
                .includeUppercase(true)
                .build();

        return ResponseEntity.ok(generatorService.generate(request));
    }

    // POST: Check strength
    @PostMapping("/strength")
    public ResponseEntity<PasswordStrengthResponse> checkStrength(
            @RequestBody PasswordStrengthRequest request) {
        return ResponseEntity.ok(generatorService.checkStrength(request.getPassword()));
    }
}
