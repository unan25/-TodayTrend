package com.todaytrend.userservice.controller;

import com.todaytrend.userservice.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/server")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ServerController {

    private final ServerService serverService;
    @GetMapping("nickname/{nickName}")
    public ResponseEntity<?> findUuid(@PathVariable("nickName")String nickName){
        return new ResponseEntity<>(serverService.findUuid(nickName), HttpStatus.OK);
    }

    @GetMapping("uuid/{UUID}")
    public ResponseEntity<?> findImgAndNickname(@PathVariable("UUID")String UUID){
        return new ResponseEntity<>(serverService.findImgAndNickname(UUID), HttpStatus.OK);
    }

}
