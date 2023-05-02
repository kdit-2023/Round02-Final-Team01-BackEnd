package shop.donutmarket.donut.domain.myPage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.myPage.dto.MyPageResp;
import shop.donutmarket.donut.domain.myPage.service.MyPageService;
import shop.donutmarket.donut.global.auth.MyUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myPages")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/boards")
    public ResponseEntity<?> myBoards(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MyPageResp.MyBoardDTO myBoardDTOS = myPageService.나의게시글보기(myUserDetails.getUser().getId());
        return ResponseEntity.ok(myBoardDTOS);
    }

    @GetMapping("/payments")
    public ResponseEntity<?> myPayments(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MyPageResp.MyPaymentDTO myPaymentDTOS = myPageService.나의구매내역보기(myUserDetails.getUser().getId());
        return ResponseEntity.ok(myPaymentDTOS);
    }

    @GetMapping("/blacklists")
    public ResponseEntity<?> myBlacklists(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MyPageResp.MyBlacklistDTO myBlacklistDTOS = myPageService.나의블랙리스트보기(myUserDetails.getUser().getId());
        return ResponseEntity.ok(myBlacklistDTOS);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> myReports(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MyPageResp.MyReportDTO myReportDTOS = myPageService.나의신고내역보기(myUserDetails.getUser().getId());
        return ResponseEntity.ok(myReportDTOS);
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> myReviews(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MyPageResp.MyReviewDTO myReviewDTO = myPageService.나의리뷰목록보기(myUserDetails);
        return ResponseEntity.ok(myReviewDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> myProfile(@RequestBody String imgPath, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        myPageService.프로필변경(imgPath, myUserDetails);
        return ResponseEntity.ok().body("프로필 변경에 성공했습니다.");
    }
}
