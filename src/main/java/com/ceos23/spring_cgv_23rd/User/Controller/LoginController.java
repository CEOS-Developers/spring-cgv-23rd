package com.ceos23.spring_cgv_23rd.User.Controller;

import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginResponseDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.UserWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Service.LoginService;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<UserWrapperDTO> login(
            @RequestBody LoginRequestDTO req,
            HttpServletResponse res
    ){
        return ResponseEntity.ok(loginService.login(req, res));

    }

}
