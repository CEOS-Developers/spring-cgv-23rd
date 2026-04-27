package com.ceos23.spring_boot.domain.user.repository;

import com.ceos23.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Boolean existsByEmailAndDeletedAtIsNull(String email);
}
