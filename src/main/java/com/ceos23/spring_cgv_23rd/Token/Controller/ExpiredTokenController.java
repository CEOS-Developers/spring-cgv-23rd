package com.ceos23.spring_cgv_23rd.Token.Controller;

import com.ceos23.spring_cgv_23rd.Token.DTO.ExpiredTokenResponseDTO;
import com.ceos23.spring_cgv_23rd.Token.Service.ExpiredTokenService;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
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

    /**
     * 만료된 기존 토큰을 새 토큰으로 교체합니다.
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @return 새 토큰에 대한 정보
     */
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
