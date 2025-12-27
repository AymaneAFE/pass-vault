package passvault.vaultservice.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import passvault.vaultservice.dto.CategoryResponse;
import passvault.vaultservice.dto.VaultEntryRequest;
import passvault.vaultservice.dto.VaultEntryResponse;
import passvault.vaultservice.entity.Category;
import passvault.vaultservice.entity.VaultEntry;
import passvault.vaultservice.exception.ResourceNotFoundException;
import passvault.vaultservice.repository.CategoryRepository;
import passvault.vaultservice.repository.VaultEntryRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class VaultEntryService {

  private final VaultEntryRepository vaultEntryRepository;
  private final CategoryRepository categoryRepository;
  private final EncryptionService encryptionService;

  @Transactional(readOnly = true)
  public List<VaultEntryResponse> listEntries(UUID userId) {
    Objects.requireNonNull(userId, "userId is required");
    return vaultEntryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public VaultEntryResponse getEntry(UUID id, UUID userId) {
    Objects.requireNonNull(userId, "userId is required");
    VaultEntry entry = vaultEntryRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Vault entry not found"));
    return toResponse(entry);
  }

  public VaultEntryResponse createEntry(UUID userId, VaultEntryRequest request) {
    Objects.requireNonNull(userId, "userId is required");

    Category category = resolveCategory(request.getCategoryId(), userId);

    VaultEntry entry = VaultEntry.builder()
        .userId(userId)
        .title(request.getTitle())
        .usernameEncrypted(encryptionService.encrypt(request.getUsername()))
        .passwordEncrypted(encryptionService.encrypt(request.getPassword()))
        .url(request.getUrl())
        .notesEncrypted(encryptionService.encrypt(request.getNotes()))
        .category(category)
        .build();

    VaultEntry saved = vaultEntryRepository.save(entry);
    return toResponse(saved);
  }

  public VaultEntryResponse updateEntry(UUID id, UUID userId, VaultEntryRequest request) {
    Objects.requireNonNull(userId, "userId is required");
    VaultEntry entry = vaultEntryRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Vault entry not found"));

    Category category = resolveCategory(request.getCategoryId(), userId);

    entry.setTitle(request.getTitle());
    entry.setUsernameEncrypted(encryptionService.encrypt(request.getUsername()));
    entry.setPasswordEncrypted(encryptionService.encrypt(request.getPassword()));
    entry.setUrl(request.getUrl());
    entry.setNotesEncrypted(encryptionService.encrypt(request.getNotes()));
    entry.setCategory(category);

    VaultEntry saved = vaultEntryRepository.save(entry);
    return toResponse(saved);
  }

  public void deleteEntry(UUID id, UUID userId) {
    Objects.requireNonNull(userId, "userId is required");
    VaultEntry entry = vaultEntryRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Vault entry not found"));
    vaultEntryRepository.delete(entry);
  }

  private Category resolveCategory(UUID categoryId, UUID userId) {
    if (categoryId == null) {
      return null;
    }
    return categoryRepository.findByIdAndUserId(categoryId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
  }

  private VaultEntryResponse toResponse(VaultEntry entry) {
    CategoryResponse categoryResponse = null;
    if (entry.getCategory() != null) {
      Category cat = entry.getCategory();
      categoryResponse = CategoryResponse.builder()
          .id(cat.getId())
          .userId(cat.getUserId())
          .name(cat.getName())
          .build();
    }

    return VaultEntryResponse.builder()
        .id(entry.getId())
        .userId(entry.getUserId())
        .title(entry.getTitle())
        .username(encryptionService.decrypt(entry.getUsernameEncrypted()))
        .password(encryptionService.decrypt(entry.getPasswordEncrypted()))
        .url(entry.getUrl())
        .notes(encryptionService.decrypt(entry.getNotesEncrypted()))
        .category(categoryResponse)
        .createdAt(entry.getCreatedAt())
        .updatedAt(entry.getUpdatedAt())
        .build();
  }
}
