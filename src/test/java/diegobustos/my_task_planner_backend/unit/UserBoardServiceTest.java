package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.entity.UserBoard;
import diegobustos.my_task_planner_backend.repository.UserBoardRepository;
import diegobustos.my_task_planner_backend.service.UserBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserBoardServiceTest {

    @InjectMocks
    UserBoardService userBoardService;

    @Mock
    private UserBoardRepository userBoardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserBoard_success() {
        User user = User.builder().id(1L).email("test@example.com").build();
        Board board = Board.builder().id(1L).title("Test Board").build();

        when(userBoardRepository.save(any(UserBoard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserBoard result = userBoardService.createUserBoard(user, board);

        assertEquals(user, result.getUser());
        assertEquals(board, result.getBoard());
        assertTrue(user.getBoards().contains(result));
        assertTrue(board.getUsers().contains(result));
        verify(userBoardRepository).save(result);
    }

    @Test
    void createUserBoard_nullUser() {
        Board board = Board.builder().id(1L).title("Test Board").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userBoardService.createUserBoard(null, board));
        assertEquals("User cannot be null", ex.getMessage());
    }

    @Test
    void createUserBoard_nullBoard() {
        User user = User.builder().id(1L).email("test@example.com").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userBoardService.createUserBoard(user, null));
        assertEquals("Board cannot be null", ex.getMessage());
    }
}
