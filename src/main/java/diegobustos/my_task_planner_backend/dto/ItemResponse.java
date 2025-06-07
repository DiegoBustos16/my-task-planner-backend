package diegobustos.my_task_planner_backend.dto;

import diegobustos.my_task_planner_backend.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ItemResponse {
    @Schema(description = "Item ID.", example = "1")
    private Long id;

    @Schema(description = "Item Title.", example = "Item Number 1")
    private String title;

    @Schema(description = "Indicates whether the Item is completed.", example = "true")
    private boolean itemChecked;

    public static ItemResponse fromEntity(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .itemChecked(item.isItemChecked())
                .build();
    }
}
