package com.ceos.spring_cgv_23rd.domain.user.application.port.out;

import com.ceos.spring_cgv_23rd.domain.user.domain.User;

import java.util.Optional;

public interface UserPersistencePort {

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

    boolean existsById(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByNickname(String nickname);

    User save(User user);
}
