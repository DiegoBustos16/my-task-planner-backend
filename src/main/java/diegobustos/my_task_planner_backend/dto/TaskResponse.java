package diegobustos.my_task_planner_backend.dto;

import diegobustos.my_task_planner_backend.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TaskResponse {
    @Schema(description = "Task ID.", example = "1")
    private Long id;

    @Schema(description = "Task Title.", example = "Daily Task")
    private String title;

    public static  TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .build();
    }
}
