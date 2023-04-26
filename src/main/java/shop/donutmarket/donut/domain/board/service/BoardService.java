package shop.donutmarket.donut.domain.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardDeleteReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSaveReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardUpdateReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardSaveRespDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardUpdateRespDTO;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.board.model.Tag;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.board.repository.EventRepository;
import shop.donutmarket.donut.domain.board.repository.TagRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.global.auth.MyUserDetails;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;

    @Transactional
    public BoardSaveRespDTO 공고작성(BoardSaveReqDTO boardSaveReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        // event 먼저 save
        Event event = boardSaveReqDTO.toEventEntity();
        event = eventRepository.save(event);
        User user = myUserDetails.getUser();
        // image base64화
        // String image = null;
        // try {
        //     image = MyBase64Decoder.saveImage(boardSaveReqDTO.getImg());
        // } catch (IOException e) {
        //     // Exception 처리 필요
        // }
        Board board = boardSaveReqDTO.toBoardEntity(event, boardSaveReqDTO.getImg(), user);
        board = boardRepository.save(board);

        // tag save
        List<Tag> tagList = new ArrayList<>();
        for (String comment : boardSaveReqDTO.getComment()) {
            Tag tag = Tag.builder().boardId(board.getId()).comment(comment)
            .createdAt(LocalDateTime.now()).build();
            tagRepository.save(tag);
            tagList.add(tag);
        }

        BoardSaveRespDTO boardSaveRespDTO = new BoardSaveRespDTO(board, tagList);

        return boardSaveRespDTO;
    }

    public Board 상세보기(Long id) {
        Optional<Board> boardOptional = boardRepository.findByIdWithAll(id);
        Board boardPS = boardOptional.get();

        // if (boardPS.getStatusCode().getId() == 203) {
        //     // 해당 게시글은 삭제되었습니다. 리턴
        // }

        User organizer = boardPS.getOrganizer();
        Event event = boardPS.getEvent();
        Board board = Board.builder().id(boardPS.getId()).category(boardPS.getCategory()).title(boardPS.getTitle())
        .organizer(organizer).content(boardPS.getContent()).img(boardPS.getImg()).event(event).statusCode(boardPS.getStatusCode())
        .state(boardPS.getState()).city(boardPS.getCity()).town(boardPS.getTown()).createdAt(boardPS.getCreatedAt()).build();
        return board;
    }

    @Transactional
    public BoardUpdateRespDTO 업데이트(BoardUpdateReqDTO boardUpdateReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        User userOP = myUserDetails.getUser();
        Optional<Board> boardOP = boardRepository.findByIdWithEvent(boardUpdateReqDTO.getId());
        if(!boardOP.isPresent()){
            // 없음 예외처리
        }
        Board boardPS = boardOP.get();

        // 권한 체크
		if(!(boardPS.getOrganizer().getId() == userOP.getId())){
			// 권한 없음 처리
		}

        if (boardPS.getStatusCode().getId() == 203) {
            // 해당 게시글은 삭제되었습니다. 리턴
        }

        boardPS.getEvent().updateEvent(
            boardUpdateReqDTO.getQty(),boardUpdateReqDTO.getPaymentType(),
            boardUpdateReqDTO.getStartAt(),boardUpdateReqDTO.getEndAt()
        );

        BoardUpdateRespDTO boardUpdateRespDTO = new BoardUpdateRespDTO();
        List<String> tagList = new ArrayList<>();
        for (String comment : boardUpdateReqDTO.getComment()) {
            if(comment.isBlank()){
                break;
            }
            Tag tag = Tag.builder().boardId(boardPS.getId()).comment(comment)
            .createdAt(LocalDateTime.now()).build();
            tagRepository.save(tag);
            tagList.add(comment);
        }

        boardUpdateRespDTO.updateRespDTO(boardUpdateReqDTO.getQty(),boardUpdateReqDTO.getPaymentType(),
        boardUpdateReqDTO.getStartAt(),boardUpdateReqDTO.getEndAt(),boardUpdateReqDTO.getPrice(), tagList);

        return boardUpdateRespDTO;
    }
    

    @Transactional
    public void 삭제(BoardDeleteReqDTO boardDeleteReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        User userOP = myUserDetails.getUser();
        Optional<Board> boardOP = boardRepository.findById(boardDeleteReqDTO.getBoardId());
        if(!boardOP.isPresent()){
            // 없음 예외처리
        }
        Board boardPS = boardOP.get();

        // 권한 체크
		if(boardPS.getOrganizer().getId() == userOP.getId()){
			// 권한 없음 처리
		}
        
        if (boardPS.getStatusCode().getId() == 203) {
            // 해당 게시글은 삭제되었습니다. 리턴
        }

        boardPS.deleteBoard();
    }
}
