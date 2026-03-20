package com.ceos23.spring_boot.domain.user.repository;

import com.ceos23.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
