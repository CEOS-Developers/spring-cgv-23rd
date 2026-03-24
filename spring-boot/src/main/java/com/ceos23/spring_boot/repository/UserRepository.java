package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}