package com.ceos23.cgv_clone.user.repository;

import com.ceos23.cgv_clone.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
