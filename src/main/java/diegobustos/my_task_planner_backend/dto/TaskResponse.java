package diegobustos.my_task_planner_backend.dto;

import diegobustos.my_task_planner_backend.entity.Item;
import diegobustos.my_task_planner_backend.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class TaskResponse {

    @Schema(description = "Task ID.", example = "1")
    private Long id;

    @Schema(description = "Task Title.", example = "Daily Task")
    private String title;

    @Schema(description = "Indicates whether the task is completed.", example = "true")
    private boolean completed;

    @Schema(description = "List of items related to the task.")
    private List<ItemResponse> items;


    public static TaskResponse fromEntity(Task task, List<Item> items) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .completed(task.isCompleted())
                .items(items.stream().map(ItemResponse::fromEntity).toList())
                .build();
    }
}
