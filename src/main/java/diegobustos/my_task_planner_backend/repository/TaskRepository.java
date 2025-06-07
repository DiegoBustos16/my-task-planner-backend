package diegobustos.my_task_planner_backend.repository;

import diegobustos.my_task_planner_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndDeletedAtIsNull(Long id);

    List<Task> findByBoardIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long id);
}
