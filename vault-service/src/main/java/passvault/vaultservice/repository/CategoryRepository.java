package passvault.vaultservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import passvault.vaultservice.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
