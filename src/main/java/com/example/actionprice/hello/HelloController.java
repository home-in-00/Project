package com.example.actionprice.hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
* @author 연상훈
* @created 24/10/01 14:20
* @updated 24/10/01 14:20
* @info 테스트용 코드
*/

@Controller
public class HelloController {

    @GetMapping("/tempGenerateToken")
    public RedirectView goTempGenerateToken() {
        return new RedirectView("/tempGenerateToken.html");
    }
}