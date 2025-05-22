package diegobustos.my_task_planner_backend.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import diegobustos.my_task_planner_backend.controller.AuthController;
import diegobustos.my_task_planner_backend.dto.AuthRequest;
import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.dto.RegisterRequest;
import diegobustos.my_task_planner_backend.service.AuthService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockitoBean
    UserService userService;
    @MockitoBean
    AuthService authService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    UserDetailsService userDetailsService;

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class Register {

        @Test
        @DisplayName("when payload valid then 200 and token")
        void whenValidRegister_then200() throws Exception {
            RegisterRequest req = RegisterRequest.builder()
                    .firstName("A").lastName("B")
                    .email("a@b.com").password("Password1")
                    .build();
            when(userService.registerUser(any()))
                    .thenReturn(new AuthResponse("tok"));

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("tok"));
        }

        @Test
        @DisplayName("when payload invalid then 400")
        void whenInvalidRegister_then400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("when email duplicate then 400 and message")
        void whenDuplicateEmail_then400() throws Exception {
            RegisterRequest req = RegisterRequest.builder()
                    .firstName("A").lastName("B")
                    .email("dup@b.com").password("Password1")
                    .build();
            doThrow(new IllegalArgumentException("Email already in use"))
                    .when(userService).registerUser(any());

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already in use"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            RegisterRequest req = RegisterRequest.builder()
                    .firstName("A").lastName("B")
                    .email("err@b.com").password("Password1")
                    .build();
            doThrow(new RuntimeException("boom"))
                    .when(userService).registerUser(any());

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Something went wrong"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("when valid login then 200 and token")
        void whenValidLogin_then200() throws Exception {
            AuthRequest req = AuthRequest.builder()
                    .email("a@b.com").password("Password1")
                    .build();
            when(authService.login(any()))
                    .thenReturn(new AuthResponse("tok-login"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("tok-login"));
        }

        @Test
        @DisplayName("when payload invalid then 400")
        void whenInvalidLogin_then400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("when bad credentials then 401")
        void whenBadCredentials_then401() throws Exception {
            AuthRequest req = AuthRequest.builder()
                    .email("a@b.com").password("wrongPassword")
                    .build();
            doThrow(new org.springframework.security.authentication.BadCredentialsException("bad"))
                    .when(authService).login(any());

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        @Test
        @DisplayName("when user not found then 401")
        void whenUserNotFound_then401() throws Exception {
            AuthRequest req = AuthRequest.builder()
                    .email("nouser@b.com").password("anyPassword")
                    .build();
            doThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("no"))
                    .when(authService).login(any());

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        @Test
        @DisplayName("when service error then 500")
        void whenServiceError_then500() throws Exception {
            AuthRequest req = AuthRequest.builder()
                    .email("err@b.com").password("anyPassword")
                    .build();
            doThrow(new RuntimeException("boom"))
                    .when(authService).login(any());

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Something went wrong"));
        }
    }
}

