package passvault.vaultservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "vault_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaultEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String usernameEncrypted;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String passwordEncrypted;

  private String url;

  @Column(columnDefinition = "TEXT")
  private String notesEncrypted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
