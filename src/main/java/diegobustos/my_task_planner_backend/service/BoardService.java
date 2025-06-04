package diegobustos.my_task_planner_backend.service;

import diegobustos.my_task_planner_backend.dto.BoardRequest;
import diegobustos.my_task_planner_backend.dto.BoardResponse;
import diegobustos.my_task_planner_backend.entity.Board;
import diegobustos.my_task_planner_backend.entity.User;
import diegobustos.my_task_planner_backend.entity.UserBoard;
import diegobustos.my_task_planner_backend.exception.BoardNotFoundException;
import diegobustos.my_task_planner_backend.exception.UserNotFoundException;
import diegobustos.my_task_planner_backend.repository.BoardRepository;
import diegobustos.my_task_planner_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final UserBoardService userBoardService;
    private final BoardRepository boardRepository;


    @Transactional
    public BoardResponse createBoard(BoardRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Board board = Board.builder()
                .title(request.getTitle())
                .build();

        boardRepository.save(board);

        userBoardService.createUserBoard(user, board);

        return BoardResponse.fromEntity(board);
    }

    public Page<BoardResponse> getAllBoards(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Board> boards = boardRepository.findByUserEmail(email, pageable);
        return boards.map(BoardResponse::fromEntity);
    }

    public BoardResponse updateBoard(Long boardId, BoardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Board board = user.getBoards().stream()
                .map(UserBoard::getBoard)
                .filter(b -> b.getId().equals(boardId) && b.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        board.setTitle(request.getTitle());
        boardRepository.save(board);

        return BoardResponse.fromEntity(board);
    }

    public void deleteBoard(Long boardId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Board board = user.getBoards().stream()
                .map(UserBoard::getBoard)
                .filter(b -> b.getId().equals(boardId) && b.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        board.setDeletedAt(Instant.now());
        boardRepository.save(board);
    }
}
