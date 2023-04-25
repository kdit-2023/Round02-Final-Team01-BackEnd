package shop.donutmarket.donut.domain.myLocation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.myLocation.dto.MyLocationResp.DefaultMyLocationRespDTO;
import shop.donutmarket.donut.domain.myLocation.service.MyLocationService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;


@RestController
@RequiredArgsConstructor
@RequestMapping("/myLocation")
public class MyLocationController {
    
    private final MyLocationService myLocationService;

    @PostMapping("/default")
    public ResponseEntity<?> defaultLocation(@AuthenticationPrincipal MyUserDetails myUserDetails){
        DefaultMyLocationRespDTO defaultRespDTO = myLocationService.디폴트지역(myUserDetails);
        return new ResponseEntity<>(new ResponseDTO<>().data(defaultRespDTO), HttpStatus.CREATED);
    }

}
