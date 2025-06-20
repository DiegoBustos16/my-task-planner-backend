package diegobustos.my_task_planner_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    @Schema(description = "New user's first name.", example = "Diego")
    private String firstName;

    @Schema(description = "New user's last name.", example = "Bustos")
    private String lastName;
}
