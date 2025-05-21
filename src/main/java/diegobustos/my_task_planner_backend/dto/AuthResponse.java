package diegobustos.my_task_planner_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object for user login.")
public class AuthResponse {
    @Schema(description = "JWT token that should be included in Authorization header", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;
}