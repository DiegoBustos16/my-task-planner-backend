package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.dto.RegisterRequest;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import diegobustos.my_task_planner_backend.service.JwtService;
import diegobustos.my_task_planner_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;

    private RegisterRequest req;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        req = new RegisterRequest("A","B","a@b.com","pass");
    }

    @Test
    void register_success() {
        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");
        when(jwtService.generateToken("a@b.com"))
                .thenReturn("tok");

        AuthResponse res = userService.registerUser(req);

        assertEquals("tok", res.getToken());
        verify(userRepository).save(argThat((User u) ->
                u.getEmail().equals("a@b.com") &&
                        u.getPassword().equals("encoded")
        ));
    }

    @Test
    void register_duplicate() {
        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.of(new User()));
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(req)
        );
        assertEquals("Email already in use", ex.getMessage());
    }
}

