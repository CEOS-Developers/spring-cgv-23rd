package com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.repository.UserJpaRepository;
import com.ceos.spring_cgv_23rd.domain.user.application.port.out.UserPersistencePort;
import com.ceos.spring_cgv_23rd.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long userId) {
        return userJpaRepository.existsById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userJpaRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(userJpaRepository.save(mapper.toEntity(user)));
    }
}
