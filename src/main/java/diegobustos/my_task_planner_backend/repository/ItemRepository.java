package diegobustos.my_task_planner_backend.repository;

import diegobustos.my_task_planner_backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndDeletedAtIsNull(Long id);
    List<Item> findByTaskIdAndDeletedAtIsNull(Long taskId);
}
