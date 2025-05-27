package diegobustos.my_task_planner_backend.controller;

import diegobustos.my_task_planner_backend.dto.UpdatePasswordRequest;
import diegobustos.my_task_planner_backend.dto.UpdateUserRequest;
import diegobustos.my_task_planner_backend.dto.UserResponse;
import diegobustos.my_task_planner_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestBody @Valid UpdateUserRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return ResponseEntity.ok(userService.updateUserById(authorizationHeader, request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePasswordCurrentUser(
            @RequestBody @Valid UpdatePasswordRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        userService.updatePasswordById(authorizationHeader, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> readCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return ResponseEntity.ok(userService.getUserById(authorizationHeader));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        userService.deleteUserById(authorizationHeader);
        return ResponseEntity.noContent().build();
    }

}
