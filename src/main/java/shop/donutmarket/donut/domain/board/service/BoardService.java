package shop.donutmarket.donut.domain.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.admin.model.StatusCode;
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
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

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

        Optional<Board> boardOptional = boardRepository.findById(id);
        Board boardPS = boardOptional.get();

        Event eventPS = boardPS.getEvent();

        // if (boardPS.getStatusCode().getId() == 203) {
        //     // 해당 게시글은 삭제되었습니다. 리턴
        // }
        User userPS = userRepository.findById(boardPS.getOrganizer().getId()).get();
        Rate rate = Rate.builder().rateName(userPS.getRate().getRateName()).build();
        User organizer = User.builder().name(userPS.getName()).profile(userPS.getProfile()).rate(rate).build();
        Event event = Event.builder().latitude(eventPS.getLatitude()).longtitude(eventPS.getLongtitude())
        .qty(eventPS.getQty()).paymentType(eventPS.getPaymentType()).startAt(eventPS.getStartAt())
        .endAt(eventPS.getEndAt()).price(eventPS.getPrice()).createdAt(eventPS.getCreatedAt()).build();

        Board board = Board.builder().id(boardPS.getId()).title(boardPS.getTitle()).organizer(organizer)
        .content(boardPS.getContent()).img(boardPS.getImg()).event(event).state(boardPS.getState())
        .city(boardPS.getCity()).town(boardPS.getTown()).build();

        return board;
    }

    @Transactional
    public BoardUpdateRespDTO 업데이트(BoardUpdateReqDTO boardUpdateReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        User userOP = myUserDetails.getUser();
        Optional<Board> boardOP = boardRepository.findById(boardUpdateReqDTO.getId());
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

        boardOP.get().getEvent().updateEvent(
            boardUpdateReqDTO.getQty(),boardUpdateReqDTO.getPaymentType(),
            boardUpdateReqDTO.getStartAt(),boardUpdateReqDTO.getEndAt()
        );

        // 더디체킹

        // // image base64화
        // String image = null;
        // try {
        //     image = MyBase64Decoder.saveImage(boardUpdateReqDTO.getImg());
        // } catch (IOException e) {
        //     // Exception 처리 필요
        // }

        BoardUpdateRespDTO boardUpdateRespDTO = new BoardUpdateRespDTO();
        System.out.println("Tag");
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
        System.out.println("RespDTO");
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
        boardPS.deleteBoard();
    }
}
