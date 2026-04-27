package com.ceos23.spring_cgv_23rd.User.Service;

import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginResponseDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.UserWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.global.CookieMaker;
import com.ceos23.spring_cgv_23rd.global.DTO.TokenResultDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final TokenProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final CookieMaker cookieMaker;

    @Transactional
    public UserWrapperDTO login(LoginRequestDTO req,
                                HttpServletResponse res){

        User user = userRepository.findByLoginId(req.loginId()).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_FOUND)
        );

        if (!encoder.matches(req.password(), user.getPassword())){
            throw new CustomException(ErrorCode.UNMATCHED_PASSWORD);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLoginId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String jwtAccessToken = jwtProvider.createToken(user.getLoginId(), authentication, JWTType.ACCESS);
        String jwtRefreshToken = jwtProvider.createToken(user.getLoginId(), authentication, JWTType.REFRESH);
        TokenResultDTO resultDTO = TokenResultDTO.create(jwtAccessToken, jwtRefreshToken);

        cookieMaker.setLoginCookie(res, resultDTO);

        RefreshToken refreshToken = RefreshToken.create(user, jwtRefreshToken);
        tokenRepository.save(refreshToken);

        return UserWrapperDTO.create(user);
    }
}
