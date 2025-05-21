package diegobustos.my_task_planner_backend.integration;

import diegobustos.my_task_planner_backend.config.DotenvInitializer;
import diegobustos.my_task_planner_backend.dto.AuthRequest;
import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private RegisterRequest validRegister;
    private AuthRequest validLogin;

    static {
        new DotenvInitializer();
    }

        @BeforeEach
    void setUp() {
        validRegister = RegisterRequest.builder()
                .firstName("Name")
                .lastName("Lastname")
                .email("test@example.com")
                .password("Password123")
                .build();

        validLogin = AuthRequest.builder()
                .email("test@example.com")
                .password("Password123")
                .build();
    }

    @Test
    void givenValidRegisterRequest_whenRegisterAndLogin_thenReturnDifferentValidTokens() {
        // 1) Register
        ResponseEntity<AuthResponse> regRes = restTemplate.postForEntity(
                "/api/v1/auth/register",
                validRegister,
                AuthResponse.class
        );
        assertEquals(HttpStatus.OK, regRes.getStatusCode());
        assertNotNull(regRes.getBody());
        String tokenReg = regRes.getBody().getToken();
        assertNotNull(tokenReg);
        assertFalse(tokenReg.isEmpty());

        // 2) Login
        ResponseEntity<AuthResponse> logRes = restTemplate.postForEntity(
                "/api/v1/auth/login",
                validLogin,
                AuthResponse.class
        );
        assertEquals(HttpStatus.OK, logRes.getStatusCode());
        assertNotNull(logRes.getBody());
        String tokenLog = logRes.getBody().getToken();
        assertNotNull(tokenLog);
        assertFalse(tokenLog.isEmpty());

        // 3) Verify tokens are different
        assertNotEquals(tokenReg, tokenLog);
    }

    @Test
    void givenEmptyRegisterRequest_whenRegister_thenReturnValidationErrors() {
        // Payload empty
        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/v1/auth/register",
                new RegisterRequest(),
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        String body = res.getBody();
        assertTrue(body.contains("firstName"));
        assertTrue(body.contains("lastName"));
        assertTrue(body.contains("email"));
        assertTrue(body.contains("password"));
    }

    @Test
    void givenEmailAlreadyRegistered_whenRegister_thenReturnEmailAlreadyInUseError() {
        // First registry
        restTemplate.postForEntity("/api/v1/auth/register", validRegister, AuthResponse.class);

        // Second registry with the same email
        ResponseEntity<String> res2 = restTemplate.postForEntity(
                "/api/v1/auth/register",
                validRegister,
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, res2.getStatusCode());
        assertTrue(res2.getBody().contains("Email already in use"));
    }

    @Test
    void givenWrongPassword_whenLogin_thenReturnUnauthorizedWithInvalidCredentialsMessage() {
        // Login with the wrong password
        AuthRequest bad = AuthRequest.builder()
                .email("diego@example.com")
                .password("wrong_password")
                .build();
        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/v1/auth/login",
                bad,
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertTrue(res.getBody().contains("Invalid email or password"));
    }

    @Test
    void givenUnregisteredEmail_whenLogin_thenReturnUnauthorizedWithUserNotFoundMessage() {
        AuthRequest req = AuthRequest.builder()
                .email("nouser@example.com")
                .password("any_password")
                .build();
        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/v1/auth/login",
                req,
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertTrue(res.getBody().contains("Invalid email or password"));
    }
}

