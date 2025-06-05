package diegobustos.my_task_planner_backend.service;

import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.entity.UserBoard;
import diegobustos.my_task_planner_backend.repository.UserBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBoardService {

    private final UserBoardRepository userBoardRepository;

    public UserBoard createUserBoard(User user, Board board) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }

        UserBoard userBoard = UserBoard.builder()
                .user(user)
                .board(board)
                .build();

        userBoardRepository.save(userBoard);

        board.getUsers().add(userBoard);
        user.getBoards().add(userBoard);

        return userBoard;
    }
}
