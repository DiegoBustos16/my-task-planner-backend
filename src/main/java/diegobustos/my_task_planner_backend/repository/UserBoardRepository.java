package diegobustos.my_task_planner_backend.repository;

import diegobustos.my_task_planner_backend.entity.UserBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {
}
