package diegobustos.my_task_planner_backend.unit;

import diegobustos.my_task_planner_backend.dto.ItemRequest;
import diegobustos.my_task_planner_backend.dto.TaskResponse;
import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.Item;
import diegobustos.my_task_planner_backend.entity.Task;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.TaskNotFoundException;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.repository.BoardRepository;
import diegobustos.my_task_planner_backend.repository.ItemRepository;
import diegobustos.my_task_planner_backend.repository.TaskRepository;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import diegobustos.my_task_planner_backend.service.ItemService;
import diegobustos.my_task_planner_backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock private UserRepository userRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private BoardRepository boardRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private TaskService taskService;

    private final String email = "test@example.com";
    private final User user = User.builder().id(1L).email(email).build();
    private final Board board = Board.builder().id(1L).users(List.of()).build();
    private final Task task = Task.builder().id(1L).board(board).completed(false).build();
    private final TaskResponse mockResponse = TaskResponse.fromEntity(task, List.of());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(email, null));
    }

    @Test
    void createItem_success() {
        ItemRequest request = new ItemRequest("Item Title");

        Item item = new Item();
        item.setId(1L);
        item.setTitle("Sample Item");
        item.setItemChecked(true);

        List<Item> items = List.of(item);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.of(board));
        when(taskService.mapTaskToResponse(task)).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            return TaskResponse.fromEntity(t, items);
        });

        TaskResponse result = itemService.createItem(task.getId(), request);

        assertThat(result).isInstanceOf(TaskResponse.class);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_userNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(taskRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(task));

        assertThrows(UserNotFoundException.class, () -> itemService.createItem(task.getId(), new ItemRequest("")));
    }

    @Test
    void createItem_taskNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.createItem(task.getId(), new ItemRequest("")));
    }

    @Test
    void createItem_boardNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> itemService.createItem(task.getId(), new ItemRequest("")));
    }

    @Test
    void updateItem_success() {
        Item item = Item.builder().id(1L).title("old").task(task).build();
        ItemRequest request = new ItemRequest("updated");

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.of(board));
        when(taskService.mapTaskToResponse(task)).thenReturn(mockResponse);

        TaskResponse response = itemService.updateItem(item.getId(), request);

        assertEquals(mockResponse, response);
        assertEquals("updated", item.getTitle());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_itemNotFound() {
        when(itemRepository.findByIdAndDeletedAtIsNull(123L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.updateItem(123L, new ItemRequest("fail")));
    }

    @Test
    void updateItem_userNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(item.getId(), new ItemRequest("")));
    }

    @Test
    void updateItem_taskNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.updateItem(item.getId(), new ItemRequest("")));
    }

    @Test
    void updateItem_boardNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> itemService.updateItem(item.getId(), new ItemRequest("")));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void toggleItemCompletion_success(boolean initialState) {
        Task task = Task.builder().id(1L).board(board).completed(initialState).build();
        TaskResponse mockResponse = TaskResponse.fromEntity(task, List.of());
        Item item = Item.builder().id(1L).itemChecked(initialState).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.of(board));
        when(taskService.mapTaskToResponse(task)).thenReturn(mockResponse);

        TaskResponse response = itemService.toggleItemCompletion(item.getId());

        assertEquals(!initialState, item.isItemChecked());
        assertEquals(mockResponse, response);
        verify(itemRepository).save(item);
    }

    @Test
    void toggleItemCompletion_itemNotFound() {
        when(itemRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.toggleItemCompletion(99L));
    }

    @Test
    void toggleItemCompletion_userNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.toggleItemCompletion(item.getId()));
    }

    @Test
    void toggleItemCompletion_taskNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.toggleItemCompletion(item.getId()));
    }

    @Test
    void toggleItemCompletion_boardNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> itemService.toggleItemCompletion(item.getId()));
    }

    @Test
    void deleteItem_success() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId()))
                .thenReturn(Optional.of(item))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.of(board));
        when(taskService.mapTaskToResponse(task)).thenReturn(mockResponse);

        TaskResponse response = itemService.deleteItem(item.getId());

        assertNotNull(item.getDeletedAt());
        assertEquals(mockResponse, response);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteItem_itemNotFound() {
        when(itemRepository.findByIdAndDeletedAtIsNull(42L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.deleteItem(42L));
    }

    @Test
    void deleteItem_userNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.deleteItem(item.getId()));
    }

    @Test
    void deleteItem_taskNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> itemService.deleteItem(item.getId()));
    }

    @Test
    void deleteItem_boardNotFound() {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId())).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> itemService.deleteItem(item.getId()));
    }

    @ParameterizedTest
    @MethodSource("provideItemsForTaskCompletion")
    void deleteItem_taskCompletionUpdate(List<Item> items, boolean shouldToggleCompletion) {
        Item item = Item.builder().id(1L).task(task).build();

        when(itemRepository.findByIdAndDeletedAtIsNull(item.getId()))
                .thenReturn(Optional.of(item))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndDeletedAtIsNull(task.getId())).thenReturn(Optional.of(task));
        when(boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, board.getId()))
                .thenReturn(Optional.of(board));
        when(taskService.mapTaskToResponse(task)).thenReturn(mockResponse);
        when(itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId())).thenReturn(items);

        TaskResponse response = itemService.deleteItem(item.getId());

        assertNotNull(item.getDeletedAt());
        assertEquals(mockResponse, response);
        verify(itemRepository).save(item);

        if (shouldToggleCompletion) {
            verify(taskService).toggleTaskCompletion(task.getId());
        } else {
            verify(taskService, never()).toggleTaskCompletion(task.getId());
        }
    }

    static Stream<Arguments> provideItemsForTaskCompletion() {
        return Stream.of(
                Arguments.of(List.of(), false),
                Arguments.of(
                        List.of(
                                Item.builder().itemChecked(true).build(),
                                Item.builder().itemChecked(true).build()
                        ), true
                ),
                Arguments.of(
                        List.of(
                                Item.builder().itemChecked(true).build(),
                                Item.builder().itemChecked(false).build()
                        ), false
                )
        );
    }


}
