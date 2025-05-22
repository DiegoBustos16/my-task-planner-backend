package diegobustos.my_task_planner_backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID.", example = "1")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name.", example = "Diego")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name.", example = "Bustos")
    private String lastName;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User's email address.", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Schema(description = "Encrypted user password.", example = "$2a$10$...")
    private String password;
}
