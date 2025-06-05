package diegobustos.my_task_planner_backend.entity;

import diegobustos.my_task_planner_backend.entity.common.AuditableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Board ID.", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Board Title.", example = "Personal Tasks")
    private String title;

    @Builder.Default
    @OneToMany(
            mappedBy = "board",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Schema(description = "List of users associated with the board.")
    private List<UserBoard> users = new ArrayList<>();
}
