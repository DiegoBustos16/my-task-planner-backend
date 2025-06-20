package diegobustos.my_task_planner_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    @Schema(description = "Task Title.", example = "Daily Task", required = true)
    @NotBlank(message = "Title is required")
    private String title;
}
