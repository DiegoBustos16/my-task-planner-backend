package diegobustos.my_task_planner_backend.repository;

import diegobustos.my_task_planner_backend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b JOIN b.users ub WHERE ub.user.email = :email AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    Page<Board> findByUserEmail(String email, Pageable pageable);

}
