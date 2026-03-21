package com.ceos23.spring_boot.cgv.repository.user;

import com.ceos23.spring_boot.cgv.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}