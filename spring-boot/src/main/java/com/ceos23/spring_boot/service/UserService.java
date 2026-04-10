package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.MyInfoResponse;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public MyInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return MyInfoResponse.from(user);
    }
}