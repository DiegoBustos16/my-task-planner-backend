package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.BoardRequest;
import diegobustos.my_task_planner_backend.dto.BoardResponse;
import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.entity.UserBoard;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.repository.BoardRepository;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import diegobustos.my_task_planner_backend.service.BoardService;
import diegobustos.my_task_planner_backend.service.UserBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserBoardService userBoardService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createBoard_success() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        User user = User.builder().email(email).build();
        BoardRequest request = new BoardRequest("New Board");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoardResponse response = boardService.createBoard(request);

        assertEquals("New Board", response.getTitle());
        verify(boardRepository).save(any(Board.class));
        verify(userBoardService).createUserBoard(eq(user), any(Board.class));
    }

    @Test
    void createBoard_userNotFound() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        BoardRequest request = new BoardRequest("New Board");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> boardService.createBoard(request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateBoard_success() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        User user = User.builder().email(email).build();
        Board board = Board.builder().id(1L).title("Old Title").build();
        UserBoard userBoard = UserBoard.builder().user(user).board(board).build();
        user.getBoards().add(userBoard);

        BoardRequest request = new BoardRequest("New Title");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(boardRepository.save(board)).thenReturn(board);

        BoardResponse response = boardService.updateBoard(1L, request);

        assertEquals("New Title", response.getTitle());
        verify(boardRepository).save(board);
    }

    @Test
    void updateBoard_boardNotFound() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        User user = User.builder().email(email).build();
        Board board = Board.builder().id(2L).title("Old Title").build();
        UserBoard userBoard = UserBoard.builder().user(user).board(board).build();
        user.getBoards().add(userBoard);

        BoardRequest request = new BoardRequest("New Title");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        BoardNotFoundException ex = assertThrows(BoardNotFoundException.class,
                () -> boardService.updateBoard(1L, request));
        assertEquals("Board not found", ex.getMessage());
    }

    @Test
    void deleteBoard_success() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        User user = User.builder().email(email).build();
        Board board = Board.builder().id(1L).title("My Board").build();
        UserBoard userBoard = UserBoard.builder().user(user).board(board).build();
        user.getBoards().add(userBoard);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(boardRepository.save(board)).thenReturn(board);

        boardService.deleteBoard(1L);

        assertNotNull(board.getDeletedAt());
        verify(boardRepository).save(board);
    }

    @Test
    void deleteBoard_boardNotFound() {
        String email = "test@example.com";
        when(authentication.getName()).thenReturn(email);

        User user = User.builder().email(email).build();
        Board board = Board.builder().id(2L).title("My Board").build();
        UserBoard userBoard = UserBoard.builder().user(user).board(board).build();
        user.getBoards().add(userBoard);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        BoardNotFoundException ex = assertThrows(BoardNotFoundException.class,
                () -> boardService.deleteBoard(1L));
        assertEquals("Board not found", ex.getMessage());
    }
}
