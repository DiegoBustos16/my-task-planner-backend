package diegobustos.my_task_planner_backend.dto;

import diegobustos.my_task_planner_backend.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardResponse {
    @Schema(description = "Board ID.", example = "1")
    private Long id;

    @Schema(description = "Board Title.", example = "Personal Tasks")
    private String title;

    public static  BoardResponse fromEntity(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .build();
    }
}
