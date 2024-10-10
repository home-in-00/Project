package com.example.actionprice.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 연상훈
 * @created 2024-10-08 오후 4:09
 * @updated 2024-10-08 오후 4:09
 * @see : 홈으로 이동하는 요청과 경로를 명확히 하기 위한 컨트롤러입니다.
 */
@RestController
@Log4j2
@RequiredArgsConstructor
public class CommonController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> goHome(){
        Map<String, String> response = new HashMap<>();
        response.put("url", "/");
        return ResponseEntity.ok(response);
    }

}
