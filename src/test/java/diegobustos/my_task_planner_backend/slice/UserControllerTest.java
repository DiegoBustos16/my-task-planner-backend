package diegobustos.my_task_planner_backend.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import diegobustos.my_task_planner_backend.config.JwtFilter;
import diegobustos.my_task_planner_backend.controller.UserController;
import diegobustos.my_task_planner_backend.dto.UpdatePasswordRequest;
import diegobustos.my_task_planner_backend.dto.UpdateUserRequest;
import diegobustos.my_task_planner_backend.dto.UserResponse;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.service.JwtService;
import diegobustos.my_task_planner_backend.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockitoBean
    UserService userService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    JwtFilter jwtFilter;

    @Nested
    @DisplayName("GET /api/v1/user/me")
    class ReadCurrentUser {

        @Test
        @DisplayName("when user exists then 200 and body")
        void whenUserExists_then200() throws Exception {
            UserResponse response = new UserResponse("Diego", "Bustos", "a@b.com");
            when(userService.getUserById()).thenReturn(response);

            mockMvc.perform(get("/api/v1/user/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("a@b.com"));
        }

        @Test
        @DisplayName("when user not found then 404")
        void whenUserNotFound_then404() throws Exception {
            doThrow(new UserNotFoundException("User not found"))
                    .when(userService).getUserById();

            mockMvc.perform(get("/api/v1/user/me"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            doThrow(new RuntimeException())
                    .when(userService).getUserById();

            mockMvc.perform(get("/api/v1/user/me"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/user/me")
    class UpdateCurrentUser {

        @Test
        @DisplayName("when payload valid then 200 and body")
        void whenValidUpdate_then200() throws Exception {
            UpdateUserRequest req = new UpdateUserRequest("Diego", "Bustos");
            UserResponse response = new UserResponse("Diego", "Bustos", "a@b.com");
            when(userService.updateUserById(any())).thenReturn(response);

            mockMvc.perform(patch("/api/v1/user/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("a@b.com"));
        }

        @Test
        @DisplayName("when user not found then 404")
        void whenUserNotFound_then404() throws Exception {
            UpdateUserRequest req = new UpdateUserRequest("Name", "Surname");
            doThrow(new UserNotFoundException("User not found"))
                    .when(userService).updateUserById(any());

            mockMvc.perform(patch("/api/v1/user/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            UpdateUserRequest req = new UpdateUserRequest("Name", "Surname");
            doThrow(new RuntimeException())
                    .when(userService).updateUserById(any());

            mockMvc.perform(patch("/api/v1/user/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/user/me/password")
    class UpdatePassword {

        @Test
        @DisplayName("when payload valid then 204")
        void whenValidPasswordUpdate_then204() throws Exception {
            UpdatePasswordRequest req = new UpdatePasswordRequest("OldPass123", "NewPass456");

            mockMvc.perform(patch("/api/v1/user/me/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("when payload invalid then 400")
        void whenInvalidPasswordUpdate_then400() throws Exception {
            mockMvc.perform(patch("/api/v1/user/me/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("when incorrect password then 400 and message")
        void whenIncorrectPassword_then400() throws Exception {
            UpdatePasswordRequest req = new UpdatePasswordRequest("wrongPass123", "newPass123");
            doThrow(new IllegalArgumentException("The password is incorrect"))
                    .when(userService).updatePasswordById(any());

            mockMvc.perform(patch("/api/v1/user/me/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("The password is incorrect"));
        }

        @Test
        @DisplayName("when user not found then 404")
        void whenUserNotFound_then404() throws Exception {
            UpdatePasswordRequest req = new UpdatePasswordRequest("wrongPass123", "newPass123");
            doThrow(new UserNotFoundException("User not found"))
                    .when(userService).updatePasswordById(any());

            mockMvc.perform(patch("/api/v1/user/me/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            UpdatePasswordRequest req = new UpdatePasswordRequest("wrongPass123", "newPass123");
            doThrow(new RuntimeException())
                    .when(userService).updatePasswordById(any());

            mockMvc.perform(patch("/api/v1/user/me/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/user/me")
    class DeleteCurrentUser {

        @Test
        @DisplayName("when user exists then 204")
        void whenUserExists_then204() throws Exception {
            mockMvc.perform(delete("/api/v1/user/me"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("when user not found then 404")
        void whenUserNotFound_then404() throws Exception {
            doThrow(new UserNotFoundException("User not found"))
                    .when(userService).deleteUserById();

            mockMvc.perform(delete("/api/v1/user/me"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            doThrow(new RuntimeException())
                    .when(userService).deleteUserById();

            mockMvc.perform(delete("/api/v1/user/me"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }
}

