package diegobustos.my_task_planner_backend.entity.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Schema(description = "Timestamp when the entity was created.", example = "2024-05-01T12:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Schema(description = "Timestamp when the entity was last updated.", example = "2024-05-10T14:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @Column
    @Schema(description = "Timestamp when the entity was deleted.", example = "2024-06-01T15:00:00Z", nullable = true, accessMode = Schema.AccessMode.READ_ONLY)
    private Instant deletedAt;
}
