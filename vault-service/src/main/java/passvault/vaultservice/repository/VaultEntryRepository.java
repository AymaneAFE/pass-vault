package passvault.vaultservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import passvault.vaultservice.entity.VaultEntry;

public interface VaultEntryRepository extends JpaRepository<VaultEntry, UUID> {

  List<VaultEntry> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

  Optional<VaultEntry> findByIdAndUserId(UUID id, UUID userId);
}
