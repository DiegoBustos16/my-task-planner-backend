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
public class Task extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Task ID.", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Task Title.", example = "Daily Task")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @Schema(description = "Board to which this task belongs.")
    private Board board;

    @Column(nullable = false)
    @Schema(description = "Indicates whether the task is completed.", example = "false")
    private boolean completed;
}
