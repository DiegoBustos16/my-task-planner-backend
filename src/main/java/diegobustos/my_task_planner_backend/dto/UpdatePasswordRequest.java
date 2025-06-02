package diegobustos.my_task_planner_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequest {
    @Schema(description = "Current Password with at least 8 characters, letters and numbers.", example = "pass1234", required = true)
    @NotBlank(message = "Current Password is required")
    @Size(min = 8, message = "Current Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Current Password must contain letters and numbers"
    )
    private String oldPassword;

    @Schema(description = "New Password with at least 8 characters, letters and numbers.", example = "pass1234", required = true)
    @NotBlank(message = "New Password is required")
    @Size(min = 8, message = "New Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "New Password must contain letters and numbers"
    )
    private String newPassword;
}
