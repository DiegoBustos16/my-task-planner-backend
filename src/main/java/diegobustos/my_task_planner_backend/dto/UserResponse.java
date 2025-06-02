package diegobustos.my_task_planner_backend.dto;

import diegobustos.my_task_planner_backend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    @Schema(description = "User's first name.", example = "Diego")
    private String firstName;

    @Schema(description = "User's last name.", example = "Bustos")
    private String lastName;

    @Schema(description = "User's email address.", example = "user@example.com")
    private String email;

    public static  UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
