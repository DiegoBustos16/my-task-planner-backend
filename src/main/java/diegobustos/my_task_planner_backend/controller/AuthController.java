package diegobustos.my_task_planner_backend.controller;

import diegobustos.my_task_planner_backend.dto.AuthRequest;
import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.dto.RegisterRequest;
import diegobustos.my_task_planner_backend.service.AuthService;
import diegobustos.my_task_planner_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "Authenticate user and return JWT",
            description = "Authenticates a user using their email and password. Returns a JWT token if credentials are valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful. JWT token returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6...\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload (e.g., missing or blank fields).",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"email\": \"Email is required\", \"password\": \"Password is required\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed: invalid credentials or user not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Invalid email or password\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal error.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Something went wrong\"}")
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with first name, last name, email, and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6...\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Email already in use\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

}
