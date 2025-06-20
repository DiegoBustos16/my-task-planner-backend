package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.dto.RegisterRequest;
import diegobustos.my_task_planner_backend.dto.UpdatePasswordRequest;
import diegobustos.my_task_planner_backend.dto.UpdateUserRequest;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import diegobustos.my_task_planner_backend.service.JwtService;
import diegobustos.my_task_planner_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void updateUser_success() {
        mockAuthenticatedUser("a@b.com");
        var user = User.builder().email("a@b.com").firstName("Old").lastName("Name").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        var req = new UpdateUserRequest("NewFirst", "NewLast");
        var res = userService.updateUserById(req);

        assertEquals("NewFirst", res.getFirstName());
        assertEquals("NewLast", res.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_notFound() {
        mockAuthenticatedUser("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        var req = new UpdateUserRequest("A", "B");
        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(req));
    }

    @Test
    void getUser_success() {
        mockAuthenticatedUser("a@b.com");
        var user = User.builder().email("a@b.com").firstName("A").lastName("B").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        var res = userService.getUserById();

        assertEquals("A", res.getFirstName());
        assertEquals("B", res.getLastName());
    }

    @Test
    void getUser_notFound() {
        mockAuthenticatedUser("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById());
    }

    @Test
    void updatePassword_success() {
        mockAuthenticatedUser("a@b.com");
        var user = User.builder().email("a@b.com").password("hashed").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword1", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("newHashed");

        var req = new UpdatePasswordRequest("oldPassword1", "newPassword1");
        userService.updatePasswordById(req);

        assertEquals("newHashed", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_wrongPassword() {
        mockAuthenticatedUser("a@b.com");
        var user = User.builder().email("a@b.com").password("hashed").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword1", "hashed")).thenReturn(false);

        var req = new UpdatePasswordRequest("wrongPassword1", "newPassword1");

        var ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePasswordById(req));
        assertEquals("The password is incorrect", ex.getMessage());
    }

    @Test
    void updatePassword_notFound() {
        mockAuthenticatedUser("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        var req = new UpdatePasswordRequest("oldPassword1", "newPassword1");
        assertThrows(UserNotFoundException.class, () -> userService.updatePasswordById(req));
    }

    @Test
    void deleteUser_success() {
        mockAuthenticatedUser("a@b.com");
        var user = User.builder().email("a@b.com").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        userService.deleteUserById();

        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_notFound() {
        mockAuthenticatedUser("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById());
    }

    private void mockAuthenticatedUser(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }
}

