package diegobustos.my_task_planner_backend.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import diegobustos.my_task_planner_backend.config.JwtFilter;
import diegobustos.my_task_planner_backend.controller.ItemController;
import diegobustos.my_task_planner_backend.dto.ItemRequest;
import diegobustos.my_task_planner_backend.dto.TaskResponse;
import diegobustos.my_task_planner_backend.exception.TaskNotFoundException;
import diegobustos.my_task_planner_backend.service.ItemService;
import diegobustos.my_task_planner_backend.service.JwtService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    JwtFilter jwtFilter;

    @Nested
    @DisplayName("POST /api/v1/item/{id}")
    class CreateItem {

        @Test
        @DisplayName("200 when request is valid")
        void whenValidRequest_thenReturn200() throws Exception {
            ItemRequest req = new ItemRequest("My Item");
            TaskResponse resp = new TaskResponse(1L, "Task Title", false, Collections.emptyList());

            when(itemService.createItem(eq(5L), any(ItemRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/api/v1/item/5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Task Title"))
                    .andExpect(jsonPath("$.completed").value(false));
        }

        @Test
        @DisplayName("400 when title is blank")
        void whenTitleBlank_thenReturn400() throws Exception {
            ItemRequest req = new ItemRequest("");
            mockMvc.perform(post("/api/v1/item/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when task not found")
        void whenTaskNotFound_thenReturn404() throws Exception {
            ItemRequest req = new ItemRequest("Item");
            doThrow(new TaskNotFoundException("Task not found"))
                    .when(itemService).createItem(eq(2L), any(ItemRequest.class));

            mockMvc.perform(post("/api/v1/item/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            ItemRequest req = new ItemRequest("Item");
            doThrow(new RuntimeException())
                    .when(itemService).createItem(eq(3L), any(ItemRequest.class));

            mockMvc.perform(post("/api/v1/item/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/item/{id}")
    class UpdateItem {

        @Test
        @DisplayName("200 when update is valid")
        void whenValidUpdate_thenReturn200() throws Exception {
            ItemRequest req = new ItemRequest("Updated Item");
            TaskResponse resp = new TaskResponse(10L, "Task Title", true, Collections.emptyList());

            when(itemService.updateItem(eq(10L), any(ItemRequest.class))).thenReturn(resp);

            mockMvc.perform(patch("/api/v1/item/10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        @DisplayName("400 when title is blank")
        void whenTitleBlank_thenReturn400() throws Exception {
            ItemRequest req = new ItemRequest("");
            mockMvc.perform(patch("/api/v1/item/11")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when item not found")
        void whenItemNotFound_thenReturn404() throws Exception {
            ItemRequest req = new ItemRequest("X");
            doThrow(new TaskNotFoundException("Item not found"))
                    .when(itemService).updateItem(eq(12L), any(ItemRequest.class));

            mockMvc.perform(patch("/api/v1/item/12")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Item not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            ItemRequest req = new ItemRequest("X");
            doThrow(new RuntimeException())
                    .when(itemService).updateItem(eq(13L), any(ItemRequest.class));

            mockMvc.perform(patch("/api/v1/item/13")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/item/toggle/{id}")
    class ToggleItem {

        @Test
        @DisplayName("200 when toggle success")
        void whenToggle_thenReturn200() throws Exception {
            TaskResponse resp = new TaskResponse(20L, "Task Title", true, Collections.emptyList());

            when(itemService.toggleItemCompletion(20L)).thenReturn(resp);

            mockMvc.perform(patch("/api/v1/item/toggle/20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(20L))
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        @DisplayName("404 when item not found")
        void whenNotFound_thenReturn404() throws Exception {
            doThrow(new TaskNotFoundException("Item not found"))
                    .when(itemService).toggleItemCompletion(21L);

            mockMvc.perform(patch("/api/v1/item/toggle/21"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Item not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(itemService).toggleItemCompletion(22L);

            mockMvc.perform(patch("/api/v1/item/toggle/22"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/item/{id}")
    class DeleteItem {

        @Test
        @DisplayName("200 when deleted")
        void whenDeleted_thenReturn200() throws Exception {
            TaskResponse resp = new TaskResponse(30L, "Task Title", false, Collections.emptyList());

            when(itemService.deleteItem(30L)).thenReturn(resp);

            mockMvc.perform(delete("/api/v1/item/30"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(30L));
        }

        @Test
        @DisplayName("404 when item not found")
        void whenNotFound_thenReturn404() throws Exception {
            doThrow(new TaskNotFoundException("Item not found"))
                    .when(itemService).deleteItem(31L);

            mockMvc.perform(delete("/api/v1/item/31"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Item not found"));
        }

        @Test
        @DisplayName("500 on unexpected error")
        void whenUnexpected_thenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(itemService).deleteItem(32L);

            mockMvc.perform(delete("/api/v1/item/32"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }
}
