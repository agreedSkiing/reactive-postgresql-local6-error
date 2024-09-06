import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "code")
public class Code {

  @Id
  @GeneratedValue(generator = "resource-uuid")
  @UuidGenerator
  @Column(name = "resource_id", nullable = false, updatable = false)
  UUID resourceIdentifier;

  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;

  @Column(name = "modified_at")
  Instant modifiedAt;

  @Column(name = "created_by")
  String createdBy;

  @Column(name = "modified_by")
  String modifiedBy;

  @Version
  @Column(name = "version", nullable = false)
  int version; // For optimistic locking

  @Column(name = "code", nullable = false)
  String codeIdentifier;

  @Column(name = "display_name")
  String displayName;

  @Column(name = "original_text")
  String originalText;

  @Column(name = "additional_info", length = 2048)
  String additionalInfo;

  @Override
  public int hashCode() {
    return Objects.hash(resourceIdentifier);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Code)) {
      return false;
    }
    Code other = (Code) obj;
    return Objects.equals(resourceIdentifier, other.resourceIdentifier);
  }

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    modifiedAt = null;
  }

  @PreUpdate
  protected void onModify() {
    modifiedAt = Instant.now();
  }

  public UUID getResourceIdentifier() {
    return resourceIdentifier;
  }

  public void setResourceIdentifier(UUID resourceIdentifier) {
    this.resourceIdentifier = resourceIdentifier;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(Instant modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getCodeIdentifier() {
    return codeIdentifier;
  }

  public void setCodeIdentifier(String codeIdentifier) {
    this.codeIdentifier = codeIdentifier;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getOriginalText() {
    return originalText;
  }

  public void setOriginalText(String originalText) {
    this.originalText = originalText;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  private Code() {}
}
