package diegobustos.my_task_planner_backend.service;

import diegobustos.my_task_planner_backend.dto.TaskRequest;
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
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public TaskResponse createTask(Long boardId, TaskRequest request){
        Board board = validateAccessAndGetBoard(boardId);

        Task task = Task.builder()
                .title(request.getTitle())
                .board(board)
                .build();

        taskRepository.save(task);

        return mapTaskToResponse(task);
    }

    public List<TaskResponse> getAllTasks(Long id) {
        Board board = validateAccessAndGetBoard(id);

        List<Task> tasks = taskRepository.findByBoardIdAndDeletedAtIsNullOrderByCreatedAtDesc(board.getId());

        return tasks.stream().map(this::mapTaskToResponse).toList();
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request) {

        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Board board = validateAccessAndGetBoard(task.getBoard().getId());

        task.setTitle(request.getTitle());
        taskRepository.save(task);

        return mapTaskToResponse(task);
    }

    public TaskResponse toggleTaskCompletion(Long taskId) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Board board = validateAccessAndGetBoard(task.getBoard().getId());

        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);

        return mapTaskToResponse(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Board board = validateAccessAndGetBoard(task.getBoard().getId());

        task.setDeletedAt(Instant.now());
        taskRepository.save(task);
    }

    public TaskResponse mapTaskToResponse(Task task) {
        List <Item> items = itemRepository.findByTaskIdAndDeletedAtIsNull(task.getId());
        return TaskResponse.fromEntity(task, items);
    }

    private Board validateAccessAndGetBoard(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Board board = boardRepository.findByUserEmailAndBoardIdAndDeletedAtIsNull(email, id)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        return board;
    }
}
