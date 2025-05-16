package diegobustos.my_task_planner_backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user registration.")
public class RegisterRequest {

    @Schema(description = "User's first name.", example = "Diego", required = true)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User's last name.", example = "Bustos", required = true)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User's email address.", example = "user@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Password with at least 8 characters, letters and numbers.", example = "pass1234", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must contain letters and numbers"
    )
    private String password;
}

