package diegobustos.my_task_planner_backend.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import diegobustos.my_task_planner_backend.config.JwtFilter;
import diegobustos.my_task_planner_backend.controller.BoardController;
import diegobustos.my_task_planner_backend.dto.BoardRequest;
import diegobustos.my_task_planner_backend.dto.BoardResponse;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.service.BoardService;
import diegobustos.my_task_planner_backend.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BoardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    BoardService boardService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtFilter jwtFilter;

    @Nested
    @DisplayName("POST /api/v1/board")
    class CreateBoard {

        @Test
        @DisplayName("should return 200 and response body when request is valid")
        void whenValidRequest_thenReturn200() throws Exception {
            BoardRequest request = new BoardRequest("My Board");
            BoardResponse response = new BoardResponse(1L, "My Board");

            when(boardService.createBoard(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("My Board"));
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void whenTitleIsBlank_thenReturn400() throws Exception {
            BoardRequest request = new BoardRequest("");

            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void whenUserNotFound_thenReturn404() throws Exception {
            BoardRequest request = new BoardRequest("My Board");

            doThrow(new UserNotFoundException("User not found"))
                    .when(boardService).createBoard(any());

            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("should return 500 on unexpected error")
        void whenUnexpectedError_thenReturn500() throws Exception {
            BoardRequest request = new BoardRequest("My Board");

            doThrow(new RuntimeException())
                    .when(boardService).createBoard(any());

            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/board/me")
    class GetAllBoards {

        @Test
        @DisplayName("should return 200 and list of boards")
        void whenBoardsExist_thenReturn200() throws Exception {
            BoardResponse response = new BoardResponse(1L, "Board 1");
            PageImpl<BoardResponse> page = new PageImpl<>(
                    Collections.singletonList(response),
                    PageRequest.of(0, 10),
                    1
            );

            when(boardService.getAllBoards(0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/board/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].title").value("Board 1"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void whenUserNotFound_thenReturn404() throws Exception {
            doThrow(new UserNotFoundException("User not found"))
                    .when(boardService).getAllBoards(0, 10);

            mockMvc.perform(get("/api/v1/board/me"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("should return 500 on unexpected error")
        void whenUnexpectedError_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(boardService).getAllBoards(0, 10);

            mockMvc.perform(get("/api/v1/board/me"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/board/{id}")
    class UpdateBoard {

        @Test
        @DisplayName("should return 200 and updated board")
        void whenValidUpdate_thenReturn200() throws Exception {
            BoardRequest request = new BoardRequest("Updated Board");
            BoardResponse response = new BoardResponse(1L, "Updated Board");

            when(boardService.updateBoard(eq(1L), any(BoardRequest.class))).thenReturn(response);

            mockMvc.perform(patch("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Updated Board"));
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void whenTitleIsBlank_thenReturn400() throws Exception {
            BoardRequest request = new BoardRequest("");

            mockMvc.perform(patch("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 404 when board not found")
        void whenBoardNotFound_thenReturn404() throws Exception {
            BoardRequest request = new BoardRequest("Updated Board");

            doThrow(new BoardNotFoundException("Board not found"))
                    .when(boardService).updateBoard(eq(1L), any(BoardRequest.class));

            mockMvc.perform(patch("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Board not found"));
        }

        @Test
        @DisplayName("should return 500 on unexpected error")
        void whenUnexpectedError_thenReturn500() throws Exception {
            BoardRequest request = new BoardRequest("Updated Board");

            doThrow(new RuntimeException())
                    .when(boardService).updateBoard(eq(1L), any(BoardRequest.class));

            mockMvc.perform(patch("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/board/{id}")
    class DeleteBoard {

        @Test
        @DisplayName("should return 204 when board is deleted")
        void whenBoardExists_thenReturn204() throws Exception {
            mockMvc.perform(delete("/api/v1/board/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when board not found")
        void whenBoardNotFound_thenReturn404() throws Exception {
            doThrow(new BoardNotFoundException("Board not found"))
                    .when(boardService).deleteBoard(1L);

            mockMvc.perform(delete("/api/v1/board/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Board not found"));
        }

        @Test
        @DisplayName("should return 500 on unexpected error")
        void whenUnexpectedError_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(boardService).deleteBoard(1L);

            mockMvc.perform(delete("/api/v1/board/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }
}
