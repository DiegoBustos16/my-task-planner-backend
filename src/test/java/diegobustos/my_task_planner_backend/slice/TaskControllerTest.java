package diegobustos.my_task_planner_backend.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import diegobustos.my_task_planner_backend.config.JwtFilter;
import diegobustos.my_task_planner_backend.controller.TaskController;
import diegobustos.my_task_planner_backend.dto.TaskRequest;
import diegobustos.my_task_planner_backend.dto.TaskResponse;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.TaskNotFoundException;
import diegobustos.my_task_planner_backend.service.JwtService;
import diegobustos.my_task_planner_backend.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    TaskService taskService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtFilter jwtFilter;

    @Nested
    @DisplayName("POST /api/v1/task/{id}")
    class CreateTask {

        @Test
        @DisplayName("200 when request is valid")
        void whenValidRequest_thenReturn200() throws Exception {
            TaskRequest req = new TaskRequest("My Task");
            TaskResponse resp = new TaskResponse(1L, "My Task", false, Collections.emptyList());

            when(taskService.createTask(eq(5L), any())).thenReturn(resp);

            mockMvc.perform(post("/api/v1/task/5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("My Task"))
                    .andExpect(jsonPath("$.completed").value(false));
        }

        @Test
        @DisplayName("400 when title is blank")
        void whenTitleBlank_thenReturn400() throws Exception {
            TaskRequest req = new TaskRequest("");
            mockMvc.perform(post("/api/v1/task/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when board not found")
        void whenBoardNotFound_thenReturn404() throws Exception {
            TaskRequest req = new TaskRequest("Task");
            doThrow(new BoardNotFoundException("Board not found"))
                    .when(taskService).createTask(eq(2L), any());

            mockMvc.perform(post("/api/v1/task/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Board not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            TaskRequest req = new TaskRequest("Task");
            doThrow(new RuntimeException())
                    .when(taskService).createTask(eq(3L), any());

            mockMvc.perform(post("/api/v1/task/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/task/{id}")
    class GetAllTasks {

        @Test
        @DisplayName("200 when tasks exist")
        void whenTasksExist_thenReturn200() throws Exception {
            TaskResponse resp = new TaskResponse(1L, "T1", false, Collections.emptyList());
            when(taskService.getAllTasks(7L)).thenReturn(List.of(resp));

            mockMvc.perform(get("/api/v1/task/7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].title").value("T1"));
        }

        @Test
        @DisplayName("404 when board not found")
        void whenBoardNotFound_thenReturn404() throws Exception {
            doThrow(new BoardNotFoundException("Board not found"))
                    .when(taskService).getAllTasks(8L);

            mockMvc.perform(get("/api/v1/task/8"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Board not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(taskService).getAllTasks(9L);

            mockMvc.perform(get("/api/v1/task/9"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/task/{id}")
    class UpdateTask {

        @Test
        @DisplayName("200 when valid update")
        void whenValidUpdate_thenReturn200() throws Exception {
            TaskRequest req = new TaskRequest("Updated");
            TaskResponse resp = new TaskResponse(10L, "Updated", false, Collections.emptyList());
            when(taskService.updateTask(eq(10L), any())).thenReturn(resp);

            mockMvc.perform(patch("/api/v1/task/10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.title").value("Updated"));
        }

        @Test
        @DisplayName("400 when title blank")
        void whenTitleBlank_thenReturn400() throws Exception {
            TaskRequest req = new TaskRequest("");
            mockMvc.perform(patch("/api/v1/task/11")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when task not found")
        void whenTaskNotFound_thenReturn404() throws Exception {
            TaskRequest req = new TaskRequest("X");
            doThrow(new TaskNotFoundException("Task not found"))
                    .when(taskService).updateTask(eq(12L), any());

            mockMvc.perform(patch("/api/v1/task/12")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            TaskRequest req = new TaskRequest("X");
            doThrow(new RuntimeException())
                    .when(taskService).updateTask(eq(13L), any());

            mockMvc.perform(patch("/api/v1/task/13")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/task/toggle/{id}")
    class ToggleTask {

        @Test
        @DisplayName("200 when toggle success")
        void whenToggle_thenReturn200() throws Exception {
            TaskResponse resp = new TaskResponse(20L, "T", true, Collections.emptyList());
            when(taskService.toggleTaskCompletion(20L)).thenReturn(resp);

            mockMvc.perform(patch("/api/v1/task/toggle/20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(20L))
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        @DisplayName("404 when task not found")
        void whenNotFound_thenReturn404() throws Exception {
            doThrow(new TaskNotFoundException("Task not found"))
                    .when(taskService).toggleTaskCompletion(21L);

            mockMvc.perform(patch("/api/v1/task/toggle/21"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(taskService).toggleTaskCompletion(22L);

            mockMvc.perform(patch("/api/v1/task/toggle/22"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/task/{id}")
    class DeleteTask {

        @Test
        @DisplayName("204 when deleted")
        void whenDeleted_thenReturn204() throws Exception {
            mockMvc.perform(delete("/api/v1/task/30"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("404 when task not found")
        void whenNotFound_thenReturn404() throws Exception {
            doThrow(new TaskNotFoundException("Task not found"))
                    .when(taskService).deleteTask(31L);

            mockMvc.perform(delete("/api/v1/task/31"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(taskService).deleteTask(32L);

            mockMvc.perform(delete("/api/v1/task/32"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }
}
