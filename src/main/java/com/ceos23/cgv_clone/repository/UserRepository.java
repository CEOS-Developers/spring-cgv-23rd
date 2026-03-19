package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
