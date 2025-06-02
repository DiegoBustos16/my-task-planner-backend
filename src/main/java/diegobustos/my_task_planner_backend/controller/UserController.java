package diegobustos.my_task_planner_backend.controller;

import diegobustos.my_task_planner_backend.dto.UpdatePasswordRequest;
import diegobustos.my_task_planner_backend.dto.UpdateUserRequest;
import diegobustos.my_task_planner_backend.dto.UserResponse;
import diegobustos.my_task_planner_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Update current user",
            description = "Updates the details of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestBody @Valid UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserById(request));
    }

    @Operation(
            summary = "Update current user's password",
            description = "Updates the password of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Password updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload (e.g., missing or blank fields).",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"currentPassword\": \"Current password is required\", \"newPassword\": \"New password is required\"}"),
                                    @ExampleObject(value = "{\"The password is incorrect\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePasswordCurrentUser(
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        userService.updatePasswordById(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get current user details",
            description = "Retrieves the details of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> readCurrentUser() {
        return ResponseEntity.ok(userService.getUserById());
    }

    @Operation(
            summary = "Delete current user",
            description = "Deletes the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteUserById();
        return ResponseEntity.noContent().build();
    }

}
