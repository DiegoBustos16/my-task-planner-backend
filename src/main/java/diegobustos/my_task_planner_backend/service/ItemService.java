package diegobustos.my_task_planner_backend.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final ItemRepository itemRepository;
    private final TaskService taskService;

    @Transactional
    public TaskResponse createItem(Long taskId, ItemRequest request){

        Task task = validateAccessAndGetTask(taskId);

        Item item = Item.builder()
                .title(request.getTitle())
                .task(task)
                .build();

        itemRepository.save(item);

        updateTaskCompletionStatus(task);

        return taskService.mapTaskToResponse(task);
    }

    public TaskResponse updateItem(Long itemId, ItemRequest request){
        Item item = itemRepository.findByIdAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new TaskNotFoundException("Item not found"));

        Task task = validateAccessAndGetTask(item.getTask().getId());

        item.setTitle(request.getTitle());
        itemRepository.save(item);

        return taskService.mapTaskToResponse(task);
    }

    public TaskResponse toggleItemCompletion(Long itemId) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new TaskNotFoundException("Item not found"));

        Task task = validateAccessAndGetTask(item.getTask().getId());

        item.setItemChecked(!item.isItemChecked());
        itemRepository.save(item);

        updateTaskCompletionStatus(task);

        return taskService.mapTaskToResponse(task);
    }

    public TaskResponse deleteItem(Long itemId) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new TaskNotFoundException("Item not found"));

        Task task = validateAccessAndGetTask(item.getTask().getId());

        item.setDeletedAt(Instant.now());
        itemRepository.save(item);

        updateTaskCompletionStatus(task);

        return taskService.mapTaskToResponse(task);
    }

    private void updateTaskCompletionStatus(Task task) {
        List<Item> items = itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId());
        boolean allItemsChecked = !items.isEmpty() && items.stream().allMatch(Item::isItemChecked);

        if (task.isCompleted() != allItemsChecked) {
            taskService.toggleTaskCompletion(task.getId());
        }
    }

    private Task validateAccessAndGetTask(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Task task = taskRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Board board = boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, task.getBoard().getId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        return task;
    }
}
