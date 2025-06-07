package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.TaskRequest;
import diegobustos.my_task_planner_backend.dto.TaskResponse;
import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.Task;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.TaskNotFoundException;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.repository.BoardRepository;
import diegobustos.my_task_planner_backend.repository.ItemRepository;
import diegobustos.my_task_planner_backend.repository.TaskRepository;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import diegobustos.my_task_planner_backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private final String userEmail = "test@example.com";
    private final User user = User.builder().id(1L).email(userEmail).build();
    private final Board board = Board.builder().id(1L).users(List.of()).build();
    private final Task task = Task.builder().id(1L).title("Sample Task").board(board).build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
    }

    @Test
    void createTask_success() {
        TaskRequest request = new TaskRequest("New Task");

        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.of(board));

        Task savedTask = Task.builder().id(1L).title("New Task").board(board).build();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(itemRepository.findByTaskIdAndDeletedAtIsNull(any())).thenReturn(List.of());

        TaskResponse response = taskService.createTask(board.getId(), request);

        assertEquals("New Task", response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_boardNotFound() {
        TaskRequest request = new TaskRequest("New Task");

        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () ->
                taskService.createTask(board.getId(), request));
    }

    @Test
    void getAllTasks_success() {
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.of(board));

        when(taskRepository.findByBoardIdAndDeletedAtIsNullOrderByCreatedAtDesc(board.getId()))
                .thenReturn(List.of(task));

        when(itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(List.of());

        List<TaskResponse> responses = taskService.getAllTasks(board.getId());

        assertEquals(1, responses.size());
    }

    @Test
    void updateTask_success() {
        TaskRequest request = new TaskRequest("Updated Task");

        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.of(board));
        when(itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(List.of());

        TaskResponse response = taskService.updateTask(task.getId(), request);

        assertEquals("Updated Task", response.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_notFound() {
        TaskRequest request = new TaskRequest("Updated Task");

        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () ->
                taskService.updateTask(task.getId(), request));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void toggleTaskCompletion_success(boolean initialState) {
        task.setCompleted(initialState);

        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.of(board));
        when(itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(List.of());

        TaskResponse response = taskService.toggleTaskCompletion(task.getId());
        assertEquals(!initialState, task.isCompleted());
        assertEquals(task.isCompleted(), response.isCompleted());
    }

    @Test
    void toggleTaskCompletion_taskNotFound() {
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () ->
                taskService.toggleTaskCompletion(task.getId()));
    }

    @Test
    void deleteTask_success() {
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.of(board));

        taskService.deleteTask(task.getId());

        assertNotNull(task.getDeletedAt());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_taskNotFound() {
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () ->
                taskService.deleteTask(task.getId()));
    }

    @Test
    void deleteTask_boardNotFound() {
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId()))
                .thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(userEmail, board.getId()))
                .thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () ->
                taskService.deleteTask(task.getId()));
    }

    @Test
    void validateAccessAndGetBoard_userNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                taskService.getAllTasks(1L));
    }
}

