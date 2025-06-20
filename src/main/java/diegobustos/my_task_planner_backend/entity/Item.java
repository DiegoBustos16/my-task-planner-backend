package diegobustos.my_task_planner_backend.entity;

import diegobustos.my_task_planner_backend.entity.common.AuditableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Item ID.", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Item Title.", example = "Item Number 1")
    private String title;

    @Column(nullable = false)
    @Schema(description = "Whether the item is checked", example = "true")
    private boolean itemChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @Schema(description = "The task this item belongs to")
    private Task task;
}

