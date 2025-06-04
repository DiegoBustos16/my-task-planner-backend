package diegobustos.my_task_planner_backend.controller;

import diegobustos.my_task_planner_backend.dto.BoardRequest;
import diegobustos.my_task_planner_backend.dto.BoardResponse;
import diegobustos.my_task_planner_backend.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardService boardService;

    @Operation (
            summary = "Create board",
            description = "Creates a new board for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses (value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Board created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload (e.g., missing or blank fields).",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Title is required\"}")
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
    @PostMapping()
    public ResponseEntity<BoardResponse> createBoard(
            @RequestBody @Valid BoardRequest request
    ) {
        return ResponseEntity.ok(boardService.createBoard(request));
    }

    @Operation(
            summary = "Get all boards",
            description = "Retrieves all boards for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Boards retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponse.class)
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
    public ResponseEntity<Page<BoardResponse>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(boardService.getAllBoards(page, size));
    }

    @Operation(
            summary = "Update board by Id",
            description = "Updates a board by its ID for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Board updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload (e.g., missing or blank fields).",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Title is required\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Board not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Board not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })

    @PatchMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @RequestBody @Valid BoardRequest request
    ) {
        return ResponseEntity.ok(boardService.updateBoard(id, request));
    }

    @Operation(
            summary = "Delete board by Id",
            description = "Deletes a board by its ID for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Board deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Board not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Board not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
