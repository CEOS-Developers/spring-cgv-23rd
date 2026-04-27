package com.ceos23.spring_cgv_23rd.Token.Controller;

import com.ceos23.spring_cgv_23rd.Token.DTO.ExpiredTokenResponseDTO;
import com.ceos23.spring_cgv_23rd.Token.Service.ExpiredTokenService;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginResponseDTO;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
public class ExpiredTokenController {

    private final TokenProvider tokenProvider;
    private final ExpiredTokenService expiredTokenService;

    public ExpiredTokenController(TokenProvider tokenProvider, ExpiredTokenService expiredTokenService) {
        this.tokenProvider = tokenProvider;
        this.expiredTokenService = expiredTokenService;
    }

    @GetMapping
    public ResponseEntity<ExpiredTokenResponseDTO> getNewTokens(
            HttpServletRequest req,
            HttpServletResponse res
    ){
        String refreshToken = tokenProvider.getRefreshToken(req);
        ExpiredTokenResponseDTO newTokens = expiredTokenService.rotateRefreshToken(refreshToken, res);

        return ResponseEntity.ok(newTokens);
    }
}
