package com.ceos23.spring_cgv_23rd.global;

import com.ceos23.spring_cgv_23rd.global.DTO.TokenResultDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieMaker {

    public void setLoginCookie(HttpServletResponse res,
                               TokenResultDTO resultDTO){
        String at = resultDTO.accessToken();
        String rt = resultDTO.refreshToken();

        Cookie cookie = new Cookie("accessToken", at);
        cookie.setMaxAge(JWTType.ACCESS.getValidTime());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
        //cookie.setSecure(true); TODO: localhost 환경을 벗어나면 주석제거

        Cookie cookie2 = new Cookie("refreshToken", rt);
        cookie2.setMaxAge(JWTType.REFRESH.getValidTime());
        cookie2.setPath("/");
        cookie2.setHttpOnly(true);
        cookie2.setAttribute("SameSite", "Strict");
        //cookie2.setSecure(true); TODO: localhost 환경을 벗어나면 주석제거

        res.addCookie(cookie);
        res.addCookie(cookie2);
    }
}
