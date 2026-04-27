package com.ceos23.spring_cgv_23rd.User.Repository;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPassword(String password);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginIdAndPassword(String loginId, String password);

    Optional<User> findByLoginId(String loginId);
}
