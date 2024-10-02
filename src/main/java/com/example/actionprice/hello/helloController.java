package com.example.actionprice.hello;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author 연상훈
* @created 24/10/01 14:20
* @updated 24/10/01 14:20
* @info 테스트용 코드
*/


@RestController
@RequestMapping("/hello") 
public class helloController {
    
    @GetMapping("/getTest")
    public ResponseEntity<String> getTest() {
        return ResponseEntity.ok("Test successful");
    }
}