package diegobustos.my_task_planner_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Column(nullable = false)
    @Schema(description = "Item Title.", example = "Item Number 1", required = true)
    @NotBlank(message = "Title is required")
    private String title;
}