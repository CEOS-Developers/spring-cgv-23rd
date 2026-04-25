package com.ceos23.spring_cgv_23rd.User.Service;

import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.User.DTO.ROLE;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupResponseDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.UserWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.CookieMaker;
import com.ceos23.spring_cgv_23rd.global.DTO.TokenResultDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SignupService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final CookieMaker cookieMaker;

    public SignupService(PasswordEncoder encoder,
                         UserRepository userRepository, UserDetailsService userDetailsService, TokenProvider tokenProvider, CookieMaker cookieMaker){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.cookieMaker = cookieMaker;
    }

    @Transactional
    public UserWrapperDTO signup(SignupRequestDTO req,
                                 HttpServletResponse res){
        String encryptedPassword = encoder.encode(req.password());

        passwordValidation(req.password());

        if (userRepository.existsByUsername(req.username())){
            throw new CustomException(ErrorCode.USER_NAME_ALREADY_EXIST);
        }

        if (userRepository.existsByLoginId(req.loginId())){
            throw new CustomException(ErrorCode.LOGIN_ID_ALREADY_EXIST);
        }

        User user = User.create(
                req.loginId(), req.username(), encryptedPassword, req.men(), req.age()
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLoginId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        UserWrapperDTO userWrapperDTO = UserWrapperDTO.create(userRepository.save(user));

        String jwtAccessToken = tokenProvider.createToken(user.getLoginId(), authentication, JWTType.ACCESS);
        String jwtRefreshToken = tokenProvider.createToken(user.getLoginId(), authentication, JWTType.REFRESH);
        TokenResultDTO resultDTO = TokenResultDTO.create(jwtAccessToken, jwtRefreshToken);

        cookieMaker.setLoginCookie(res, resultDTO);

        return userWrapperDTO;
    }

    private void passwordValidation(String password){
        if (password.length() < 8){
            throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        }
        if (!password.contains("*") && !password.contains("!")){
            throw new CustomException(ErrorCode.PASSWORD_TOO_SIMPLE);
        }
    }
}
