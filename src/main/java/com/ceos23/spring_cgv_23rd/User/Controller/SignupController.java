package com.ceos23.spring_cgv_23rd.User.Controller;

import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupResponseDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.UserWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Service.SignupService;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public ResponseEntity<UserWrapperDTO> signup(
            HttpServletResponse res,
            @RequestBody SignupRequestDTO req){

        return ResponseEntity.ok(signupService.signup(req, res));
    }
}
