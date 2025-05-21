package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.AuthRequest;
import diegobustos.my_task_planner_backend.dto.AuthResponse;
import diegobustos.my_task_planner_backend.service.AuthService;
import diegobustos.my_task_planner_backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;
    @Mock
    JwtService jwtService;

    private AuthRequest req;
    private UserDetails userDetails;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        req = new AuthRequest("a@b.com", "pass");
        userDetails = User.withUsername("a@b.com")
                .password("encoded")
                .build();
    }

    @Test
    void login_success() {
        when(userDetailsService.loadUserByUsername("a@b.com"))
                .thenReturn(userDetails);
        when(jwtService.generateToken("a@b.com"))
                .thenReturn("tok");

        AuthResponse res = authService.login(req);
        assertEquals("tok", res.getToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("a@b.com","pass")
        );
    }

    @Test
    void login_userNotFound() {
        when(userDetailsService.loadUserByUsername(any()))
                .thenThrow(new UsernameNotFoundException("no"));
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(req)
        );
        assertEquals("no", ex.getMessage());
    }

    @Test
    void login_badCredentials() {
        when(userDetailsService.loadUserByUsername("a@b.com"))
                .thenReturn(userDetails);
        doThrow(new BadCredentialsException("bad"))
                .when(authenticationManager)
                .authenticate(any());
        BadCredentialsException ex = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(req)
        );
        assertEquals("bad", ex.getMessage());
    }
}

