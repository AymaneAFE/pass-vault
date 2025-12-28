package passvault.vaultservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import passvault.vaultservice.dto.VaultEntryRequest;
import passvault.vaultservice.dto.VaultEntryResponse;
import passvault.vaultservice.service.VaultEntryService;

@RestController
@RequestMapping("/api/vault/entries")
@RequiredArgsConstructor
public class VaultEntryController {

  private final VaultEntryService vaultEntryService;

  @GetMapping
  public List<VaultEntryResponse> listEntries(@RequestHeader("X-User-Id") UUID userId) {
    return vaultEntryService.listEntries(userId);
  }

  @GetMapping("/{id}")
  public VaultEntryResponse getEntry(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
    return vaultEntryService.getEntry(id, userId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public VaultEntryResponse createEntry(@RequestHeader("X-User-Id") UUID userId,
      @Valid @RequestBody VaultEntryRequest request) {
    return vaultEntryService.createEntry(userId, request);
  }

  @PutMapping("/{id}")
  public VaultEntryResponse updateEntry(@PathVariable UUID id,
      @RequestHeader("X-User-Id") UUID userId,
      @Valid @RequestBody VaultEntryRequest request) {
    return vaultEntryService.updateEntry(id, userId, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEntry(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
    vaultEntryService.deleteEntry(id, userId);
  }
}
